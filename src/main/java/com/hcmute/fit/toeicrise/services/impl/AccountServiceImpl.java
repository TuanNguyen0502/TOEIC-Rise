package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.utils.CodeGeneratorUtils;
import com.hcmute.fit.toeicrise.dtos.requests.authentication.RegisterRequest;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Account;
import com.hcmute.fit.toeicrise.models.enums.EAuthProvider;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.AccountMapper;
import com.hcmute.fit.toeicrise.repositories.AccountRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements IAccountService {
    private final PasswordEncoder passwordEncoder;
    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;
    @Value("${security.jwt.refresh-token.expiration}")
    private Long refreshTokenDurationMs;
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int MAX_RESEND_OTP_ATTEMPTS = 5;
    private static final int MAX_RESEND_LOCK_MINUTES = 30;
    private static final int LOGIN_LOCK_MINUTES = 30;
    private static final int VERIFY_CODE_MINUTES = 5;

    @Override
    public Account createLocalAccount(RegisterRequest registerRequest) {
        Account account = accountMapper.toAccount(registerRequest);
        account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        account.setVerificationCode(CodeGeneratorUtils.generateVerificationCode());
        account.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(VERIFY_CODE_MINUTES));
        account.setIsActive(false);
        account.setAuthProvider(EAuthProvider.LOCAL);
        return account;
    }

    @Override
    public Account createGoogleAccount(String email, String fullName, String avatar) {
        return Account.builder()
                            .isActive(true)
                .email(email)
                .authProvider(EAuthProvider.GOOGLE)
                .password("{oauth2}")
                .build();
    }

    @Override
    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email).orElse(null);
    }

    @Override
    public Account saveAccount(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public Account findByRefreshToken(String refreshToken) {
        return accountRepository.findByRefreshToken(refreshToken).orElse(null);
    }

    @Override
    public boolean existsByEmail(String email) {
        return accountRepository.existsByEmail(email);
    }

    @Override
    public void registerFailedLoginAttempt(Account account) {
        account.setFailedLoginAttempts(account.getFailedLoginAttempts() + 1);
        if (account.getFailedLoginAttempts() >= MAX_LOGIN_ATTEMPTS) {
            account.setAccountLockedUntil(LocalDateTime.now().plusMinutes(LOGIN_LOCK_MINUTES));
            saveAccount(account);
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }
        saveAccount(account);
    }

    @Override
    public Account resetLoginAttempts(Account account) {
        account.setFailedLoginAttempts(0);
        account.setAccountLockedUntil(null);
        return saveAccount(account);
    }

    @Override
    public boolean isBefore(Account account, LocalDateTime time) {
        return account.getResendVerificationLockedUntil() != null &&
                LocalDateTime.now().isBefore(time);
    }

    @Override
    public void increaseResendAttempt(Account account) {
        account.setResendVerificationAttempts(account.getResendVerificationAttempts() + 1);
        if (account.getResendVerificationAttempts() >= MAX_RESEND_OTP_ATTEMPTS) {
            account.setResendVerificationLockedUntil(LocalDateTime.now().plusMinutes(MAX_RESEND_LOCK_MINUTES));
            account.setResendVerificationAttempts(0);
            throw new AppException(ErrorCode.OTP_LIMIT_EXCEEDED, MAX_RESEND_OTP_ATTEMPTS);
        }
    }

    @Override
    public void setNewOtp(Account account) {
        account.setVerificationCode(CodeGeneratorUtils.generateVerificationCode());
        account.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(VERIFY_CODE_MINUTES));
    }

    @Override
    public Account createRefreshToken(Account account) {
        String refreshToken = UUID.randomUUID().toString();
        account.setRefreshToken(refreshToken);
        account.setRefreshTokenExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        return saveAccount(account);
    }

    @Override
    public Long getRefreshTokenDurationMs() {
        return refreshTokenDurationMs;
    }
}
