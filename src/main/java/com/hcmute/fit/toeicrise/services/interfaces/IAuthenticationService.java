package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.authentication.*;
import com.hcmute.fit.toeicrise.dtos.requests.user.UserChangePasswordRequest;
import com.hcmute.fit.toeicrise.dtos.responses.authentication.CurrentUserResponse;
import com.hcmute.fit.toeicrise.dtos.responses.authentication.LoginResponse;
import com.hcmute.fit.toeicrise.dtos.responses.authentication.RefreshTokenResponse;
import com.hcmute.fit.toeicrise.dtos.responses.statistic.RegSourceInsightResponse;
import com.hcmute.fit.toeicrise.models.enums.ERole;

import java.time.LocalDateTime;

public interface IAuthenticationService {
    void register(RegisterRequest input);

    LoginResponse login(LoginRequest loginRequest);

    LoginResponse loginWithGoogle(String email, String fullName, String avatar);

    void verifyUser(VerifyUserRequest input);

    void resendVerificationCode(ResendOTPRequest request);

    void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

    String verifyOtp(OtpRequest otp);

    void resetPassword(ResetPasswordRequest resetPasswordRequest, String token);

    RefreshTokenResponse refreshToken(String refreshToken);

    String createRefreshTokenWithEmail(String email);

    String createRefreshTokenWithRefreshToken(String refreshToken);

    long getRefreshTokenDurationMs();

    CurrentUserResponse getCurrentUser(String email);

    void changePassword(UserChangePasswordRequest userChangePasswordRequest, String email);

    Long countAllUsersWithRole(ERole role);

    Long countUsersBetweenDays(LocalDateTime from, LocalDateTime to);

    Long countActiveUser(LocalDateTime from, LocalDateTime to);

    RegSourceInsightResponse getRegSourceInsight(LocalDateTime from, LocalDateTime to);
}
