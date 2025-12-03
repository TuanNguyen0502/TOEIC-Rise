package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.authentication.*;
import com.hcmute.fit.toeicrise.dtos.requests.user.UserChangePasswordRequest;
import com.hcmute.fit.toeicrise.dtos.responses.authentication.CurrentUserResponse;
import com.hcmute.fit.toeicrise.dtos.responses.authentication.LoginResponse;
import com.hcmute.fit.toeicrise.dtos.responses.authentication.RefreshTokenResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Account;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.*;
import com.hcmute.fit.toeicrise.models.mappers.UserMapper;
import com.hcmute.fit.toeicrise.services.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements IAuthenticationService {
    private final IAccountService accountService;
    private final IUserService userService;
    private final IRoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final IEmailService emailService;
    private final IJwtService jwtService;
    private final IRedisService redisService;
    private final UserMapper userMapper;
    private static final int MAX_RESEND_OTP_ATTEMPTS = 5;

    @Override
    public boolean register(RegisterRequest input) {
        if (!input.getPassword().equals(input.getConfirmPassword())) throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        if (accountService.existsByEmail(input.getEmail())) throw new AppException(ErrorCode.DUPLICATE_EMAIL);

        Account account = accountService.createLocalAccount(input);

        redisService.put(ECacheDuration.CACHE_REGISTRATION.getCacheName(),
                input.getEmail(), account, ECacheDuration.CACHE_REGISTRATION.getDuration());
        redisService.put(ECacheDuration.CACHE_FULLNAME_REGISTRATION.getCacheName(),
                input.getEmail(), input.getFullName(), ECacheDuration.CACHE_FULLNAME_REGISTRATION.getDuration());

        emailService.sendVerificationEmail(account);
        return true;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Account authenticatedUser = authenticate(loginRequest);
        return getLoginResponse(authenticatedUser);
    }

    @Override
    public LoginResponse loginWithGoogle(String email, String fullName, String avatar) {
        Account authenticatedUser = loginAndRegisterWithGoogle(email, fullName, avatar);
        return getLoginResponse(authenticatedUser);
    }

    private LoginResponse getLoginResponse(Account authenticatedUser) {
        User user = userService.findByAccount_Id(authenticatedUser.getId());
        if (user == null) throw new AppException(ErrorCode.INVALID_CREDENTIALS);
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

    private Account authenticate(LoginRequest input) {
        Account account = accountService.findByEmail(input.getEmail());
        if (account == null) throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        if (!account.isEnabled()) throw new AppException(ErrorCode.UNVERIFIED_ACCOUNT);
        if (!account.isAccountNonLocked()) throw new AppException(ErrorCode.ACCOUNT_LOCKED);

        if (!passwordEncoder.matches(input.getPassword(), account.getPassword())) {
            accountService.registerFailedLoginAttempt(account);
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmail(),
                            input.getPassword()
                    )
            );
            return accountService.resetLoginAttempts(account);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public Account loginAndRegisterWithGoogle(String email, String fullName, String avatar) {
        Account account = accountService.findByEmail(email);
        if (account == null) {
            account = accountService.createGoogleAccount(email, fullName, avatar);
            User user = userMapper.toUserEntity(fullName, roleService.findByName(ERole.LEARNER), account, EGender.OTHER);
            account.setUser(user);
            accountService.saveAccount(account);
        }
        return account;
    }

    @Override
    public void verifyUser(VerifyUserRequest input) {
        Account account = redisService.get(ECacheDuration.CACHE_REGISTRATION.getCacheName(), input.getEmail(), Account.class);
        String fullName = redisService.get(ECacheDuration.CACHE_FULLNAME_REGISTRATION.getCacheName(), input.getEmail(), String.class);
        if (account == null) throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Account");
        if (accountService.isBefore(account, LocalDateTime.now())) throw new AppException(ErrorCode.OTP_EXPIRED);
        if (!account.getVerificationCode().equals(input.getVerificationCode())) throw new AppException(ErrorCode.INVALID_OTP, "Register's");

        account.setIsActive(true);
        account.setVerificationCode(null);
        account.setVerificationCodeExpiresAt(null);

        User user = userMapper.toUserEntity(fullName, roleService.findByName(ERole.LEARNER), account, EGender.OTHER);
        account.setUser(user);
        accountService.saveAccount(account);

        redisService.remove(ECacheDuration.CACHE_REGISTRATION.getCacheName(), input.getEmail());
        redisService.remove(ECacheDuration.CACHE_FULLNAME_REGISTRATION.getCacheName(), input.getEmail());
    }

    @Override
    public void resendVerificationCode(String email) {
        Account account = accountService.findByEmail(email);
        boolean isRegister = false;
        if (account == null) {
            account = redisService.get(ECacheDuration.CACHE_REGISTRATION.getCacheName(), email, Account.class);
            isRegister = true;
        }
        if (account == null) throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        if (accountService.isBefore(account, account.getResendVerificationLockedUntil())) throw new AppException(ErrorCode.OTP_LIMIT_EXCEEDED, MAX_RESEND_OTP_ATTEMPTS);

        accountService.increaseResendAttempt(account);
        accountService.setNewOtp(account);
        emailService.sendVerificationEmail(account);
        if (isRegister) {
            redisService.put(ECacheDuration.CACHE_REGISTRATION.getCacheName(), account.getEmail(), account, ECacheDuration.CACHE_REGISTRATION.getDuration());
        }
        else accountService.saveAccount(account);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        Account account = accountService.findByEmail(forgotPasswordRequest.getEmail());
        if (account == null) throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Account");
        if (accountService.isBefore(account, account.getResendVerificationLockedUntil())) throw new AppException(ErrorCode.OTP_LIMIT_EXCEEDED, MAX_RESEND_OTP_ATTEMPTS);        accountService.increaseResendAttempt(account);
        accountService.setNewOtp(account);
        accountService.saveAccount(account);
        emailService.sendVerificationEmail(account);
    }

    @Override
    public String verifyOtp(OtpRequest otp) {
        Account account = accountService.findByEmail(otp.getEmail());

        if (account == null) throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Account");
        if (!account.getVerificationCode().equals(otp.getOtp())) throw new AppException(ErrorCode.INVALID_OTP, "User's");
        if (accountService.isBefore(account, LocalDateTime.now())) throw new AppException(ErrorCode.OTP_EXPIRED);

        account.setVerificationCode(null);
        account.setVerificationCodeExpiresAt(null);
        accountService.saveAccount(account);
        return jwtService.generateTokenResetPassword(account);
    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest, String token) {
        if (!resetPasswordRequest.getConfirmPassword().equals(resetPasswordRequest.getPassword())) throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        if (!jwtService.isPasswordResetTokenValid(token)) throw new AppException(ErrorCode.TOKEN_EXPIRED);

        String emailToken = jwtService.extractUsername(token);
        Account account = accountService.findByEmail(emailToken);

        if (account == null) throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Account");
        account.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        accountService.saveAccount(account);
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {
        Account account = accountService.findByRefreshToken(refreshToken);
        if (account == null) throw new AppException(ErrorCode.UNAUTHENTICATED);
        if (account.getRefreshTokenExpiryDate().isBefore(Instant.now())) throw new AppException(ErrorCode.TOKEN_EXPIRED);

        String newAccessToken = jwtService.generateToken(account);

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .accessTokenExpirationTime(jwtService.getExpirationTime())
                .build();
    }

    @Override
    public String createRefreshTokenWithEmail(String email) {
        Account account = accountService.findByEmail(email);
        if (account == null) throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Account");

        return accountService.createRefreshToken(account).getRefreshToken();
    }

    @Override
    public String createRefreshTokenWithRefreshToken(String refreshToken) {
        Account account = accountService.findByRefreshToken(refreshToken);
        if (account == null) throw new AppException(ErrorCode.UNAUTHENTICATED);

        return accountService.createRefreshToken(account).getRefreshToken();
    }

    @Override
    public CurrentUserResponse getCurrentUser(String email) {
        Account account = accountService.findByEmail(email);
        if (account == null) throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Account");
        User user = userService.findByAccount_Id(account.getId());
        if (user == null) throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User");
        return userMapper.toCurrentUserResponse(user);
    }

    @Override
    public void changePassword(UserChangePasswordRequest userChangePasswordRequest, String email) {
        Account account = accountService.findByEmail(email);
        if (account == null) throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Account");
        if (!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), account.getPassword())) throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        if (!userChangePasswordRequest.getNewPassword().equals(userChangePasswordRequest.getConfirmPassword())) throw new AppException(ErrorCode.PASSWORD_MISMATCH);

        account.setPassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
        accountService.saveAccount(account);
    }
}
