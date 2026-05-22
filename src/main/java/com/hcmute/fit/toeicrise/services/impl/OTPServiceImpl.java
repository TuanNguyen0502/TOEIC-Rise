package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.utils.CodeGeneratorUtils;
import com.hcmute.fit.toeicrise.dtos.requests.authentication.ResendOTPRequest;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Account;
import com.hcmute.fit.toeicrise.models.enums.ECacheDuration;
import com.hcmute.fit.toeicrise.models.enums.EOTPPurpose;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.services.interfaces.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements IOTPService {
    private final IRedisService redisService;
    private final IEmailService emailService;
    private final IAccountService accountService;

    @Override
    @Transactional
    public Account sendOTP(String email, EOTPPurpose purpose, Account account, boolean isRegister) {
        log.info("Sending OTP to email: {}, purpose: {}", email, purpose);
        validationResendLock(account);
        incrementResendAttempts(account, isRegister);

        String verificationCode = CodeGeneratorUtils.generateVerificationCode();
        account.setVerificationCode(verificationCode);
        account.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(Constant.OTP_EXPIRATION_MINUTES));
        emailService.sendVerificationEmail(account);
        redisService.put(ECacheDuration.CACHE_LIMIT_VERIFY_OTP.getCacheName(), email, 0,
                ECacheDuration.CACHE_LIMIT_VERIFY_OTP.getDuration());
        log.info("OTP sent successfully for email: {}, purpose: {}", email, purpose);
        return account;
    }

    @Override
    @Transactional
    public Account verifyOTP(Account account, String otp) {
        if (account.getVerificationCode() == null||!Objects.equals(account.getVerificationCode(), otp)) {
            handleInvalidOtpAttempt(account.getEmail());
            throw new AppException(ErrorCode.INVALID_OTP, "User's");
        }
        validateOTPExpiration(account);

        redisService.remove(ECacheDuration.CACHE_LIMIT_VERIFY_OTP.getCacheName(), account.getEmail());
        account.setVerificationCode(null);
        account.setVerificationCodeExpiresAt(null);
        account.setResendVerificationAttempts(0);
        account.setResendVerificationLockedUntil(null);
        return accountService.save(account);
    }

    @Override
    public void validateOTPExpiration(Account account) {
        if (account.getVerificationCodeExpiresAt() != null && account.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now()))
            throw new AppException(ErrorCode.OTP_EXPIRED);
    }

    @Override
    public void handleInvalidOtpAttempt(String email) {
        Integer times = redisService.get(ECacheDuration.CACHE_LIMIT_VERIFY_OTP.getCacheName(), email, Integer.class);
        times = (times == null ? 1 : times + 1);

        if (times > Constant.MAX_VERIFY_OTP_TIMES){
            log.warn("OTP verification limit exceeded for email: {}", email);
            throw new AppException(ErrorCode.OTP_LIMIT_EXCEEDED, Constant.MAX_VERIFY_OTP_TIMES);
        }
        redisService.put(ECacheDuration.CACHE_LIMIT_VERIFY_OTP.getCacheName(), email, times,
                ECacheDuration.CACHE_LIMIT_VERIFY_OTP.getDuration());
    }

    @Override
    public void validationResendLock(Account account) {
        if (account.getResendVerificationLockedUntil() != null &&
                LocalDateTime.now().isBefore(account.getResendVerificationLockedUntil())) {
            log.warn("OTP resend locked for email: {} until: {}", account.getEmail(), account.getResendVerificationLockedUntil());
            throw new AppException(ErrorCode.OTP_LIMIT_EXCEEDED, String.valueOf(Constant.MAX_RESEND_OTP_ATTEMPTS));
        }
    }

    @Override
    public void incrementResendAttempts(Account account, boolean isRegister) {
        int currentAttempts = account.getResendVerificationAttempts() == null ? 0 : account.getResendVerificationAttempts();
        int nextAttempts = currentAttempts + 1;
        account.setResendVerificationAttempts(nextAttempts);

        if (nextAttempts > Constant.MAX_RESEND_OTP_ATTEMPTS){
            account.setResendVerificationLockedUntil(LocalDateTime.now().plusMinutes(Constant.LOCK_DURATION_MINUTES));
            account.setResendVerificationAttempts(0);
            if (isRegister)
                redisService.put(ECacheDuration.CACHE_REGISTRATION.getCacheName(), account.getEmail(), account,
                        ECacheDuration.CACHE_REGISTRATION.getDuration());
            else accountService.save(account);
            log.warn("OTP resend limit exceeded for email: {}, locked until: {}", account.getEmail(), account.getResendVerificationLockedUntil());
            throw new AppException(ErrorCode.OTP_LIMIT_EXCEEDED, String.valueOf(Constant.MAX_RESEND_OTP_ATTEMPTS));
        }
    }

    @Override
    @Transactional
    public void resendVerificationCode(ResendOTPRequest request) {
        Account account = accountService.findByEmail(request.getEmail()).orElse(null);
        boolean isRegister = account == null;
        if (isRegister)
            account = redisService.get(ECacheDuration.CACHE_REGISTRATION.getCacheName(), request.getEmail(), Account.class);
        if (account == null)
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);

        account = sendOTP(request.getEmail(), EOTPPurpose.REGISTRATION, account, isRegister);
        if (isRegister)
            redisService.put(ECacheDuration.CACHE_REGISTRATION.getCacheName(), request.getEmail(), account,
                    ECacheDuration.CACHE_REGISTRATION.getDuration());
        else accountService.save(account);
    }
}
