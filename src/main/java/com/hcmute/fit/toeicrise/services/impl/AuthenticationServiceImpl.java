package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.authentication.*;
import com.hcmute.fit.toeicrise.dtos.requests.user.UserChangePasswordRequest;
import com.hcmute.fit.toeicrise.dtos.responses.authentication.CurrentUserResponse;
import com.hcmute.fit.toeicrise.dtos.responses.authentication.LoginResponse;
import com.hcmute.fit.toeicrise.dtos.responses.statistic.RegSourceInsightResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Account;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.*;
import com.hcmute.fit.toeicrise.models.mappers.UserMapper;
import com.hcmute.fit.toeicrise.services.interfaces.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements IAuthenticationService {
    private final IAccountService accountService;
    private final IUserService userService;
    private final IOTPService otpService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final IEmailService emailService;
    private final IRedisService redisService;
    private final IJwtService jwtService;
    private final UserMapper userMapper;

    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = 7;

    @Override
    @Transactional
    public void register(RegisterRequest input) {
        log.info("Register request for email: {}", input.getEmail());
        accountService.validatePasswordMatch(input.getPassword(), input.getConfirmPassword());
        accountService.findByEmail(input.getEmail()).ifPresent(_ -> {
            throw new AppException(ErrorCode.DUPLICATE_EMAIL);
        });

        Account account = accountService.createAccountForRegistration(input.getEmail(), input.getPassword());
        redisService.put(ECacheDuration.CACHE_REGISTRATION.getCacheName(), input.getEmail(), account,
                ECacheDuration.CACHE_REGISTRATION.getDuration());
        redisService.put(ECacheDuration.CACHE_FULLNAME_REGISTRATION.getCacheName(),
                input.getEmail(), input.getFullName(), ECacheDuration.CACHE_FULLNAME_REGISTRATION.getDuration());
        emailService.sendVerificationEmail(account);
        log.info("Registration successful for email: {}", input.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Login request for email: {}", loginRequest.getEmail());
        Account authenticatedUser = authenticate(loginRequest);
        return getLoginResponse(authenticatedUser);
    }

    private Account authenticate(LoginRequest input) {
        Account account = accountService.findByEmail(input.getEmail()).orElseThrow(() -> {
            log.warn("Login failed: Account not found for email: {}", input.getEmail());
            return new AppException(ErrorCode.INVALID_CREDENTIALS);
        });
        if (!passwordEncoder.matches(input.getPassword(), account.getPassword())) {
            accountService.handleFailedLoginAttempt(account);
            log.warn("Invalid password for email: {}", input.getEmail());
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
        if (!account.isEnabled())
            throw new AppException(ErrorCode.UNVERIFIED_ACCOUNT);
        if (!account.isAccountNonLocked()) {
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword()));
            return accountService.resetFailedLoginAttempts(account);
        } catch (Exception e) {
            log.error("Authentication failed for email: {}", input.getEmail(), e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    @Override
    @Transactional
    public LoginResponse loginWithGoogle(String email, String fullName, String avatar) {
        Account authenticatedUser = loginAndRegisterWithGoogle(email, fullName, avatar);
        return getLoginResponse(authenticatedUser);
    }

    private Account loginAndRegisterWithGoogle(String email, String fullName, String avatar) {
        return accountService.findByEmail(email).orElseGet(() -> {
            Account newAccount = accountService.createGoogleAccount(email);
            User user = userService.createUserWithGoogle(avatar, fullName, newAccount);
            newAccount.setUser(user);
            return accountService.save(newAccount);
        });
    }

    private LoginResponse getLoginResponse(Account authenticatedUser) {
        User user = userService.findAccountById(authenticatedUser.getId())
                .orElseThrow(() -> {
                    log.error("Login failed: Account not found for email: {}", authenticatedUser.getEmail());
                    return new AppException(ErrorCode.INVALID_CREDENTIALS);
                });
        String accessToken = jwtService.generateToken(authenticatedUser);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .expirationTime(jwtService.getExpirationTime())
                .userId(user.getId())
                .email(authenticatedUser.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().getName())
                .build();
    }

    @Override
    @Transactional
    public void verifyUser(VerifyUserRequest input) {
        Account account = redisService.get(ECacheDuration.CACHE_REGISTRATION.getCacheName(), input.getEmail(), Account.class);
        if (account == null){
            log.warn("Account not found in Redis for email: {}", input.getEmail());
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Account");
        }
        String fullName = redisService.get(ECacheDuration.CACHE_FULLNAME_REGISTRATION.getCacheName(), input.getEmail(), String.class);
        account = otpService.verifyOTP(account, input.getVerificationCode());
        account.setIsActive(true);
        User user = userService.createUser(account, fullName, ERole.LEARNER, EGender.OTHER);
        account.setUser(user);
        accountService.save(account);

        redisService.batch(redis -> {
            redis.delete(ECacheDuration.CACHE_REGISTRATION.getCacheName() + "::"+ input.getEmail());
            redis.delete(ECacheDuration.CACHE_FULLNAME_REGISTRATION.getCacheName() + "::"+ input.getEmail());
        });
        log.info("User verified successfully for email: {}", input.getEmail());
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        Account account = accountService.findByEmail(forgotPasswordRequest.getEmail()).orElseThrow(
                () -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Account")
        );
        account = otpService.sendOTP(forgotPasswordRequest.getEmail(), EOTPPurpose.FORGOT_PASSWORD, account, false);
        accountService.save(account);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest resetPasswordRequest, String authorization) {
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX))
            throw new AppException(ErrorCode.TOKEN_INVALID);
        String token = authorization.substring(BEARER_PREFIX_LENGTH);
        accountService.validatePasswordMatch(resetPasswordRequest.getPassword(), resetPasswordRequest.getConfirmPassword());
        if (!jwtService.isPasswordResetTokenValid(token))
            throw new AppException(ErrorCode.TOKEN_EXPIRED);

        String emailToken = jwtService.extractUsername(token);
        Account account = accountService.findByEmail(emailToken).orElseThrow(
                () -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Account")
        );

        account.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        accountService.save(account);
    }

    @Override
    @Cacheable(value = "user", key = "#email")
    public CurrentUserResponse getCurrentUser(String email) {
        Account account = accountService.findByEmail(email).orElseThrow(
                () -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Account")
        );
        User user = userService.findAccountById(account.getId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User"));
        return userMapper.toCurrentUserResponse(user);
    }

    @Override
    @Transactional
    public void changePassword(UserChangePasswordRequest userChangePasswordRequest, String email) {
        Account account = accountService.findByEmail(email).orElseThrow(
                () -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Account")
        );
        if (!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), account.getPassword()))
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        accountService.validatePasswordMatch(userChangePasswordRequest.getNewPassword(), userChangePasswordRequest.getConfirmPassword());

        account.setPassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
        accountService.save(account);
    }

    @Override
    @Transactional
    public String verifyOTP(OtpRequest otpRequest) {
        Account account = accountService.findByEmail(otpRequest.getEmail()).orElseThrow(() ->{
            log.error("Verify failed for email: {}", otpRequest.getEmail());
            return new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Account");
        });
        account = otpService.verifyOTP(account, otpRequest.getOtp());
        return jwtService.generateTokenResetPassword(account);
    }

    @Override
    public Long countActiveUser(LocalDateTime from, LocalDateTime to) {
        return accountService.countByRole_NameBetweenDays(from, to);
    }

    @Override
    @Transactional(readOnly = true)
    public RegSourceInsightResponse getRegSourceInsight(LocalDateTime from, LocalDateTime to) {
        RegSourceInsightResponse regSourceInsightResponse = accountService.countSourceInsight(from, to);
        double sum = regSourceInsightResponse.getGoogle() + regSourceInsightResponse.getEmail();
        if (sum == 0)
            return regSourceInsightResponse;
        return  RegSourceInsightResponse.builder()
                .google(Math.round(regSourceInsightResponse.getGoogle() / sum * 100))
                .email(Math.round((regSourceInsightResponse.getEmail() / sum) * 100))
                .build();
    }
}
