package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.utils.CodeGeneratorUtils;
import com.hcmute.fit.toeicrise.dtos.requests.*;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Account;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.ECacheDuration;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.repositories.AccountRepository;
import com.hcmute.fit.toeicrise.services.interfaces.*;
import jakarta.mail.MessagingException;
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
    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final IEmailService emailService;
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
        redisService.put(ECacheDuration.CACHE_REGISTRATION.getCacheName(),
                input.getEmail(), account, ECacheDuration.CACHE_REGISTRATION.getDuration());
        redisService.put(ECacheDuration.CACHE_FULLNAME_REGISTRATION.getCacheName(),
                input.getEmail(), input.getFullName(), ECacheDuration.CACHE_FULLNAME_REGISTRATION.getDuration());

        sendVerificationEmail(account);
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

    @Override
    public void verifyUser(VerifyUserRequest input) {
        Account account = redisService.get(ECacheDuration.CACHE_REGISTRATION.getCacheName(), input.getEmail(), Account.class);
        String fullName = redisService.get(ECacheDuration.CACHE_FULLNAME_REGISTRATION.getCacheName(), input.getEmail(), String.class);
        if (account != null) {
            if (account.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new AppException(ErrorCode.INVALID_OTP);
            }
            if (account.getVerificationCode().equals(input.getVerificationCode())) {
                account.setIsActive(true);
                account.setVerificationCode(null);
                account.setVerificationCodeExpiresAt(null);
                accountRepository.save(account);

                // Create associated User entity
                User user = userService.register(account, fullName);

                // Link the User entity to the Account
                account.setUser(user);

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
        if (account == null) {
            account = redisService.get(ECacheDuration.CACHE_REGISTRATION.getCacheName(), email, Account.class);
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
                accountRepository.save(account);
                throw new AppException(ErrorCode.OTP_LIMIT_EXCEEDED, "5");
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
    @Override
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        Account account = accountRepository.findByEmail(forgotPasswordRequest.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND,"Account"));
        account.setVerificationCode(CodeGeneratorUtils.generateVerificationCode());
        account.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        accountRepository.save(account);
        if (account.getResendVerificationLockedUntil() != null &&
                LocalDateTime.now().isBefore(account.getResendVerificationLockedUntil())) {
            throw new AppException(ErrorCode.OTP_LIMIT_EXCEEDED,
                    "5");
        }
        sendVerificationEmail(account);
    }

    @Override
    public String verifyOtp(OtpRequest otp) {
        Account account = accountRepository.findByEmail(otp.getEmail()).orElseThrow(()
                -> new AppException(ErrorCode.RESOURCE_NOT_FOUND,"Account"));
        if (!account.getVerificationCode().equals(otp.getOtp())){
            throw new AppException(ErrorCode.INVALID_OTP, "User's");
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

    private void sendVerificationEmail(Account account) { //TODO: Update with company logo
        Context context = new Context();
        // Set variables for the template from the POST request data
        String subject = "Account Verification";
        context.setVariable("subject", subject);
        context.setVariable("verificationCode", "VERIFICATION CODE " + account.getVerificationCode());

        try {
            emailService.sendEmail(account.getEmail(), subject, "emailTemplate", context);
        } catch (MessagingException e) {
            // Handle email sending exception
            e.printStackTrace();
        }
    }
}
