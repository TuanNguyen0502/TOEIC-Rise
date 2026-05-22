package com.hcmute.fit.toeicrise.controllers;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.dtos.requests.authentication.*;
import com.hcmute.fit.toeicrise.dtos.responses.authentication.LoginResponse;
import com.hcmute.fit.toeicrise.dtos.responses.authentication.RefreshTokenResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.services.interfaces.IAuthenticationService;
import com.hcmute.fit.toeicrise.services.interfaces.IOTPService;
import com.hcmute.fit.toeicrise.services.interfaces.IRefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final IAuthenticationService authenticationService;
    private final IRefreshTokenService refreshTokenService;
    private final IOTPService otpService;
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authenticationService.register(registerRequest);
        return ResponseEntity.ok(MessageConstant.REGISTRATION_SUCCESS);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authenticationService.login(loginRequest);
        String refreshToken = refreshTokenService.createRefreshTokenWithEmail(loginRequest.getEmail());
        Long refreshTokenExpirationTime = refreshTokenService.getRefreshTokenDurationMs();
        ResponseCookie responseCookie = createRefreshTokenCookie(refreshToken, refreshTokenExpirationTime);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(loginResponse);
    }

    @GetMapping("/login/google")
    public void loginWithGoogleAuth(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@CookieValue(value =  REFRESH_TOKEN_COOKIE_NAME) String refreshToken) {
        RefreshTokenResponse refreshTokenResponse = refreshTokenService.getRefreshToken(refreshToken);
        String newRefreshToken = refreshTokenService.createRefreshTokenWithRefreshToken(refreshToken);
        Long refreshTokenExpirationTime = refreshTokenService.getRefreshTokenDurationMs();
        ResponseCookie responseCookie = createRefreshTokenCookie(newRefreshToken, refreshTokenExpirationTime);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(refreshTokenResponse);
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken, Long expirationTime) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
//                .sameSite("Strict")
                .secure(true)
                .path("/")
                .maxAge(expirationTime/1000)
                .build();
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@Valid @RequestBody VerifyUserRequest verifyUserRequest) {
        authenticationService.verifyUser(verifyUserRequest);
        return ResponseEntity.ok(MessageConstant.ACCOUNT_VERIFICATION_SUCCESS);
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestBody ResendOTPRequest request) {
        otpService.resendVerificationCode(request);
        return ResponseEntity.ok(MessageConstant.VERIFICATION_CODE_SUCCESS);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        authenticationService.forgotPassword(forgotPasswordRequest);
        return ResponseEntity.ok(MessageConstant.VERIFICATION_CODE_SUCCESS);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody OtpRequest otpRequest) {
        return ResponseEntity.ok(authenticationService.verifyOTP(otpRequest));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest, @RequestHeader(name = "Authorization") String authorization) {
        authenticationService.resetPassword(resetPasswordRequest, authorization);
        return ResponseEntity.ok(MessageConstant.PASSWORD_RESET_SUCCESS);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        String email = SecurityUtils.getCurrentUser();
        if (email.isEmpty() || "anonymousUser".equals(email))
            throw new AppException(ErrorCode.UNAUTHORIZED, MessageConstant.INVALID_USER);
        return ResponseEntity.ok(authenticationService.getCurrentUser(email));
    }
}
