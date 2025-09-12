package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.utils.CodeGeneratorUtils;
import com.hcmute.fit.toeicrise.dtos.requests.LoginRequest;
import com.hcmute.fit.toeicrise.dtos.requests.RegisterRequest;
import com.hcmute.fit.toeicrise.dtos.requests.VerifyUserRequest;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Account;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.repositories.AccountRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IAuthenticationService;
import com.hcmute.fit.toeicrise.services.interfaces.IEmailService;
import com.hcmute.fit.toeicrise.services.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements IAuthenticationService {
    private final AccountRepository accountRepository;
    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final IEmailService emailService;

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
        accountRepository.save(account);

        // Create associated User entity
        User user = userService.register(account, input.getFullName());

        // Link the User entity to the Account
        account.setUser(user);

        emailService.sendVerificationEmail(account);
        accountRepository.save(account);
        return true;
    }

    @Override
    public Account authenticate(LoginRequest input) {
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
                throw new AppException(ErrorCode.ACCOUNT_LOCKED, "You have entered the wrong password more than 5 times. The account will be temporarily locked.");
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

    @Override
    public void verifyUser(VerifyUserRequest input) {
        Optional<Account> optionalAccount = accountRepository.findByEmail(input.getEmail());
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            if (account.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new AppException(ErrorCode.INVALID_OTP);
            }
            if (account.getVerificationCode().equals(input.getVerificationCode())) {
                account.setIsActive(true);
                account.setVerificationCode(null);
                account.setVerificationCodeExpiresAt(null);
                accountRepository.save(account);
            } else {
                throw new AppException(ErrorCode.INVALID_OTP);
            }
        } else {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    @Override
    public void resendVerificationCode(String email) {
        Optional<Account> optionalAccount = accountRepository.findByEmail(email);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();

            if (account.isEnabled()) {
                throw new AppException(ErrorCode.VERIFIED_ACCOUNT);
            }

            // Check if resend verification is locked
            if (account.getResendVerificationLockedUntil() != null &&
                LocalDateTime.now().isBefore(account.getResendVerificationLockedUntil())) {
                throw new AppException(ErrorCode.OTP_LIMIT_EXCEEDED,
                    "5");
            }

            // Increment resend attempts
            account.setResendVerificationAttempts(account.getResendVerificationAttempts() + 1);

            // If attempts reach 5, lock resend functionality for 30 minutes
            if (account.getResendVerificationAttempts() >= 5) {
                account.setResendVerificationLockedUntil(LocalDateTime.now().plusMinutes(30));
                account.setResendVerificationAttempts(0); // Reset counter after locking
                accountRepository.save(account);
                throw new AppException(ErrorCode.OTP_LIMIT_EXCEEDED,
                    "You have exceeded the verification code resend limit. Please try again after 30 minutes.");
            }

            // Proceed with resending verification code
            account.setVerificationCode(CodeGeneratorUtils.generateVerificationCode());
            account.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            emailService.sendVerificationEmail(account);
            accountRepository.save(account);
        } else {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
    }
}
