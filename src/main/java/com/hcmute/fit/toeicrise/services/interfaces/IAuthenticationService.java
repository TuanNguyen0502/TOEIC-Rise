package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.*;
import com.hcmute.fit.toeicrise.models.entities.Account;

public interface IAuthenticationService {
    boolean register(RegisterRequest input);
    Account authenticate(LoginRequest input);
    void verifyUser(VerifyUserRequest input);
    void resendVerificationCode(String email);
    void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);
    String verifyOtp(OtpRequest otp);
    void resetPassword(ResetPasswordRequest resetPasswordRequest, String token);
}
