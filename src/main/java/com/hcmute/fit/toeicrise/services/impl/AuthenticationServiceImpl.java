package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.utils.CodeGeneratorUtils;
import com.hcmute.fit.toeicrise.dtos.requests.*;
import com.hcmute.fit.toeicrise.dtos.responses.LoginResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Account;
import com.hcmute.fit.toeicrise.models.entities.RefreshToken;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.EAuthProvider;
import com.hcmute.fit.toeicrise.models.enums.ECacheDuration;
import com.hcmute.fit.toeicrise.models.enums.ERole;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.repositories.AccountRepository;
import com.hcmute.fit.toeicrise.repositories.RoleRepository;
import com.hcmute.fit.toeicrise.repositories.UserRepository;
import com.hcmute.fit.toeicrise.services.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements IAuthenticationService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final IEmailService emailService;
    private final IRefreshTokenService refreshTokenService;
    private final IJwtService jwtService;
    private final IRedisService redisService;

    @Override
    public boolean register(RegisterRequest input) {
        if (!input.getPassword().equals(input.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }

        if (accountRepository.findByEmail(input.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.DUPLICATE_EMAIL);
        }

        Account account = new Account();
        account.setEmail(input.getEmail());
        account.setPassword(passwordEncoder.encode(input.getPassword()));
        account.setVerificationCode(CodeGeneratorUtils.generateVerificationCode());
        account.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        account.setIsActive(false);
        account.setAuthProvider(EAuthProvider.LOCAL);

        redisService.put(ECacheDuration.CACHE_REGISTRATION.getCacheName(),
                input.getEmail(), account, ECacheDuration.CACHE_REGISTRATION.getDuration());
        redisService.put(ECacheDuration.CACHE_FULLNAME_REGISTRATION.getCacheName(),
                input.getEmail(), input.getFullName(), ECacheDuration.CACHE_FULLNAME_REGISTRATION.getDuration());

        emailService.sendVerificationEmail(account);
        return true;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Account authenticatedUser = this.authenticate(loginRequest);
        return getLoginResponse(authenticatedUser);
    }

    @Override
    public LoginResponse loginWithGoogle(String email, String fullName, String avatar) {
        Account authenticatedUser = this.loginAndRegisterWithGoogle(email, fullName, avatar);
        return getLoginResponse(authenticatedUser);
    }

    private LoginResponse getLoginResponse(Account authenticatedUser) {
        User user = userRepository.findByAccount_Id(authenticatedUser.getId())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));
        String accessToken = jwtService.generateToken(authenticatedUser);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authenticatedUser.getEmail());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expirationTime(jwtService.getExpirationTime())
                .userId(user.getId())
                .email(authenticatedUser.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().getName())
                .build();
    }

    private Account authenticate(LoginRequest input) {
        Account account = accountRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if (!account.isEnabled()) {
            throw new AppException(ErrorCode.UNVERIFIED_ACCOUNT);
        }

        // Check if account is locked
        if (!account.isAccountNonLocked()) {
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }

        // Explicit password check
        if (!passwordEncoder.matches(input.getPassword(), account.getPassword())) {
            // Increment failed attempts
            account.setFailedLoginAttempts(account.getFailedLoginAttempts() + 1);

            // Lock account if failed attempts >= 5
            if (account.getFailedLoginAttempts() >= 5) {
                account.setAccountLockedUntil(LocalDateTime.now().plusMinutes(30));
                accountRepository.save(account);
                throw new AppException(ErrorCode.ACCOUNT_LOCKED);
            }

            accountRepository.save(account);
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        // If we reach here, password is correct - authenticate with Spring Security
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmail(),
                            input.getPassword()
                    )
            );
            // Reset failed attempts on successful login
            account.setFailedLoginAttempts(0);
            account.setAccountLockedUntil(null);
            return accountRepository.save(account);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public Account loginAndRegisterWithGoogle(String email, String fullName, String avatar) {
        // Nếu đã tồn tại thì trả về luôn
        return accountRepository.findByEmail(email)
                .orElseGet(() -> {
                    // Nếu chưa có thì tạo mới
                    Account account = new Account();
                    account.setEmail(email);
                    account.setIsActive(true);
                    account.setAuthProvider(EAuthProvider.GOOGLE);

                    // Tạo mới user và gắn vào account
                    User user = new User();
                    user.setAvatar(avatar);
                    user.setFullName(fullName);
                    user.setRole(roleRepository.findByName(ERole.LEARNER));
                    user.setAccount(account);

                    account.setUser(user);

                    return accountRepository.save(account);
                });
    }


    @Override
    public void verifyUser(VerifyUserRequest input) {
        Account account = redisService.get(ECacheDuration.CACHE_REGISTRATION.getCacheName(), input.getEmail(), Account.class);
        String fullName = redisService.get(ECacheDuration.CACHE_FULLNAME_REGISTRATION.getCacheName(), input.getEmail(), String.class);
        if (account != null) {
            if (account.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new AppException(ErrorCode.OTP_EXPIRED);
            }
            if (account.getVerificationCode().equals(input.getVerificationCode())) {
                account.setIsActive(true);
                account.setVerificationCode(null);
                account.setVerificationCodeExpiresAt(null);

                // Create associated User entity
                User user = new User();
                user.setRole(roleRepository.findByName(ERole.LEARNER));
                user.setAccount(account);
                user.setFullName(fullName);

                // Link the User entity to the Account
                account.setUser(user);
                accountRepository.save(account);

                redisService.remove(ECacheDuration.CACHE_REGISTRATION.getCacheName(), input.getEmail());
                redisService.remove(ECacheDuration.CACHE_FULLNAME_REGISTRATION.getCacheName(), input.getEmail());
            } else {
                throw new AppException(ErrorCode.INVALID_OTP, "Register's");
            }
        } else {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND,"Account");
        }
    }

    @Override
    public void resendVerificationCode(String email) {
        Account account = accountRepository.findByEmail(email).orElse(null);
        boolean isRegister = false;
        if (account == null) {
            account = redisService.get(ECacheDuration.CACHE_REGISTRATION.getCacheName(), email, Account.class);
            isRegister = true;
        }
        if (account != null) {
            // Check if resend verification is locked
            if (account.getResendVerificationLockedUntil() != null &&
                LocalDateTime.now().isBefore(account.getResendVerificationLockedUntil())) {
                throw new AppException(ErrorCode.OTP_LIMIT_EXCEEDED, "5");
            }

            // Increment resend attempts
            account.setResendVerificationAttempts(account.getResendVerificationAttempts() + 1);

            // If attempts reach 5, lock resend functionality for 30 minutes
            if (account.getResendVerificationAttempts() >= 5) {
                account.setResendVerificationLockedUntil(LocalDateTime.now().plusMinutes(30));
                account.setResendVerificationAttempts(0); // Reset counter after locking
                if (isRegister) {
                    redisService.put(ECacheDuration.CACHE_REGISTRATION.getCacheName(), account.getEmail(), account, ECacheDuration.CACHE_REGISTRATION.getDuration());
                }
                else accountRepository.save(account);
                throw new AppException(ErrorCode.OTP_LIMIT_EXCEEDED, "5");
            }

            // Proceed with resending verification code
            account.setVerificationCode(CodeGeneratorUtils.generateVerificationCode());
            account.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            emailService.sendVerificationEmail(account);
            if (isRegister) {
                redisService.put(ECacheDuration.CACHE_REGISTRATION.getCacheName(), account.getEmail(), account, ECacheDuration.CACHE_REGISTRATION.getDuration());
            }
            else accountRepository.save(account);
        } else {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
    }
    @Override
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        Account account = accountRepository.findByEmail(forgotPasswordRequest.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND,"Account"));
        if (account.getResendVerificationLockedUntil() != null &&
                LocalDateTime.now().isBefore(account.getResendVerificationLockedUntil())) {
            throw new AppException(ErrorCode.OTP_LIMIT_EXCEEDED,
                    "5");
        }
        if (account.getResendVerificationAttempts() > 5) {
            account.setResendVerificationLockedUntil(LocalDateTime.now().plusMinutes(30));
            account.setResendVerificationAttempts(0); // Reset counter after locking
            accountRepository.save(account);
            throw new AppException(ErrorCode.OTP_LIMIT_EXCEEDED, "5");
        }
        account.setVerificationCode(CodeGeneratorUtils.generateVerificationCode());
        account.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        account.setResendVerificationAttempts(account.getResendVerificationAttempts() + 1);
        accountRepository.save(account);
        emailService.sendVerificationEmail(account);
    }

    @Override
    public String verifyOtp(OtpRequest otp) {
        Account account = accountRepository.findByEmail(otp.getEmail()).orElseThrow(()
                -> new AppException(ErrorCode.RESOURCE_NOT_FOUND,"Account"));
        if (!account.getVerificationCode().equals(otp.getOtp())){
            throw new AppException(ErrorCode.INVALID_OTP, "User's");
        }
        if (account.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.OTP_EXPIRED);
        }
        account.setVerificationCode(null);
        account.setVerificationCodeExpiresAt(null);
        accountRepository.save(account);
        return jwtService.generateTokenResetPassword(account);
    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest, String token) {
        if (!resetPasswordRequest.getConfirmPassword().equals(resetPasswordRequest.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }
        if (!jwtService.isPasswordResetTokenValid(token)){
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }
        Account account = accountRepository.findByEmail(resetPasswordRequest.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND,"Account"));
        account.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        accountRepository.save(account);
    }
}
