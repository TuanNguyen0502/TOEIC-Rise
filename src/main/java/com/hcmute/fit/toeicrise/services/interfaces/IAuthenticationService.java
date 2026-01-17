package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.authentication.*;
import com.hcmute.fit.toeicrise.dtos.requests.user.UserChangePasswordRequest;
import com.hcmute.fit.toeicrise.dtos.responses.authentication.CurrentUserResponse;
import com.hcmute.fit.toeicrise.dtos.responses.authentication.LoginResponse;
import com.hcmute.fit.toeicrise.dtos.responses.statistic.RegSourceInsightResponse;

import java.time.LocalDateTime;

public interface IAuthenticationService {
    void register(RegisterRequest input);
    LoginResponse login(LoginRequest loginRequest);
    LoginResponse loginWithGoogle(String email, String fullName, String avatar);
    void verifyUser(VerifyUserRequest input);
    void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);
    void resetPassword(ResetPasswordRequest resetPasswordRequest, String token);
    CurrentUserResponse getCurrentUser(String email);
    void changePassword(UserChangePasswordRequest userChangePasswordRequest, String email);
    String verifyOTP(OtpRequest otpRequest);
    Long countActiveUser(LocalDateTime from, LocalDateTime to);
    RegSourceInsightResponse getRegSourceInsight(LocalDateTime from, LocalDateTime to);
}
