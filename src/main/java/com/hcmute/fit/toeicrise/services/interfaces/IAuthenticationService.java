package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.authentication.*;
import com.hcmute.fit.toeicrise.dtos.requests.user.UserChangePasswordRequest;
import com.hcmute.fit.toeicrise.dtos.responses.authentication.CurrentUserResponse;
import com.hcmute.fit.toeicrise.dtos.responses.authentication.LoginResponse;
import com.hcmute.fit.toeicrise.dtos.responses.authentication.RefreshTokenResponse;

public interface IAuthenticationService {
    boolean register(RegisterRequest input);

    LoginResponse login(LoginRequest loginRequest);

    LoginResponse loginWithGoogle(String email, String fullName, String avatar);

    void verifyUser(VerifyUserRequest input);

    void resendVerificationCode(String email);

    void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

    String verifyOtp(OtpRequest otp);

    void resetPassword(ResetPasswordRequest resetPasswordRequest, String token);

    RefreshTokenResponse refreshToken(String refreshToken, String email);

    String createRefreshToken(String email);

    long getRefreshTokenDurationMs();

    CurrentUserResponse getCurrentUser(String email);

    void changePassword(UserChangePasswordRequest userChangePasswordRequest, String email);
}
