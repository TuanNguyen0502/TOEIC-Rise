package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.*;
import com.hcmute.fit.toeicrise.dtos.responses.LoginResponse;
import com.hcmute.fit.toeicrise.dtos.responses.RefreshTokenResponse;

public interface IAuthenticationService {
    boolean register(RegisterRequest input);

    LoginResponse login(LoginRequest loginRequest);

    LoginResponse loginWithGoogle(String email, String fullName, String avatar);

    void verifyUser(VerifyUserRequest input);

    void resendVerificationCode(String email);

    void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

    String verifyOtp(OtpRequest otp);

    void resetPassword(ResetPasswordRequest resetPasswordRequest, String token);

    RefreshTokenResponse refreshToken(String refreshToken);
}
