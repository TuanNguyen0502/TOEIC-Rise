package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.authentication.ResendOTPRequest;
import com.hcmute.fit.toeicrise.models.entities.Account;
import com.hcmute.fit.toeicrise.models.enums.EOTPPurpose;

public interface IOTPService {
    Account sendOTP(String email, EOTPPurpose purpose, Account account, boolean isRegister);
    Account verifyOTP(Account account, String otp);
    void validateOTPExpiration(Account account);
    void handleInvalidOtpAttempt(String email);
    void validationResendLock(Account account);
    void incrementResendAttempts(Account account, boolean isRegister);
    void resendVerificationCode(ResendOTPRequest request);
}
