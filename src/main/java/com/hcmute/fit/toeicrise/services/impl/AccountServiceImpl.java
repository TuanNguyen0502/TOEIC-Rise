package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.utils.CodeGeneratorUtils;
import com.hcmute.fit.toeicrise.dtos.responses.statistic.RegSourceInsightResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Account;
import com.hcmute.fit.toeicrise.models.enums.EAuthProvider;
import com.hcmute.fit.toeicrise.models.enums.ERole;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.repositories.AccountRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements IAccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public Account findByRefreshToken(String refreshToken) {
        return accountRepository.findByRefreshToken(refreshToken).orElse(null);
    }

    @Override
    public Long countByRole_NameBetweenDays(LocalDateTime startDate, LocalDateTime endDate) {
        return accountRepository.countByRole_NameBetweenDays(ERole.LEARNER, startDate, endDate);
    }

    @Override
    public RegSourceInsightResponse countSourceInsight(LocalDateTime from, LocalDateTime to) {
        return accountRepository.countSourceInsight(from, to, ERole.LEARNER, EAuthProvider.LOCAL, EAuthProvider.GOOGLE);
    }

    @Override
    public Account createAccountForRegistration(String email, String password) {
        return Account.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .verificationCode(CodeGeneratorUtils.generateVerificationCode())
                .verificationCodeExpiresAt(LocalDateTime.now().plusMinutes(Constant.OTP_EXPIRATION_MINUTES))
                .isActive(false)
                .authProvider(EAuthProvider.LOCAL)
                .build();
    }

    @Override
    public void handleFailedLoginAttempt(Account account) {
        int failedLoginAttempts = account.getFailedLoginAttempts() + 1;
        account.setFailedLoginAttempts(failedLoginAttempts);

        if (failedLoginAttempts > Constant.MAX_VERIFY_LOGIN_TIMES) {
            account.setAccountLockedUntil(LocalDateTime.now().plusMinutes(Constant.LOCK_DURATION_MINUTES));
            save(account);
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }
        save(account);
    }

    @Override
    public Account resetFailedLoginAttempts(Account account) {
        account.setFailedLoginAttempts(0);
        account.setAccountLockedUntil(null);
        return save(account);
    }

    @Override
    public Account createGoogleAccount(String email) {
        return Account.builder()
                .email(email)
                .isActive(true)
                .authProvider(EAuthProvider.GOOGLE)
                .password("{oauth2}")
                .build();
    }

    @Override
    public void validateRefreshToken(Account account) {
        if (account.getRefreshTokenExpiryDate().isBefore(Instant.now())) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }
    }

    @Override
    public void validatePasswordMatch(String password, String confirmPassword) {
        if (!password.equals(confirmPassword))
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
    }

    @Override
    public Long countAllUsers() {
        return accountRepository.count();
    }
}