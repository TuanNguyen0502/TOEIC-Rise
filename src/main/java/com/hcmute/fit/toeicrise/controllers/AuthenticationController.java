package com.hcmute.fit.toeicrise.controllers;

import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.dtos.requests.authentication.*;
import com.hcmute.fit.toeicrise.dtos.responses.authentication.LoginResponse;
import com.hcmute.fit.toeicrise.dtos.responses.authentication.RefreshTokenResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.services.interfaces.IAuthenticationService;
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
    private final IAuthenticationService authenticationServiceImpl;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authenticationServiceImpl.register(registerRequest);
        return ResponseEntity.ok("Registration successful. Please check your email for the verification code.");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginRequest loginRequest) {
        // 1. Xác thực người dùng và tạo JWT token
        LoginResponse loginResponse = authenticationServiceImpl.login(loginRequest);
        // 2. Tạo refresh token
        String refreshToken = authenticationServiceImpl.createRefreshToken(loginRequest.getEmail());
        long refreshTokenExpirationTime = authenticationServiceImpl.getRefreshTokenDurationMs();
        // Gửi refresh token về phía client thông qua HttpOnly Cookie (bảo mật hơn localStorage)
        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true) // Không thể đọc bằng JavaScript → tăng bảo mật
                .secure(true) // Chỉ sử dụng qua HTTPS
                .path("/") // Cookie có hiệu lực toàn bộ hệ thống
                .maxAge(refreshTokenExpirationTime) // Thời gian sống của cookie
                .build();

        // Trả về login response kèm cookie
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(loginResponse);
    }

    @GetMapping("/login/google")
    public void loginWithGoogleAuth(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@CookieValue(value = "refresh_token") String refreshToken) {
        try {

            String email = SecurityUtils.getCurrentUser();
            // 2. Tạo refresh token
            RefreshTokenResponse refreshTokenResponse = authenticationServiceImpl.refreshToken(refreshToken, email);
            // Gửi refresh token về phía client thông qua HttpOnly Cookie (bảo mật hơn localStorage)
            String newRefreshToken = authenticationServiceImpl.createRefreshToken(email);
            long refreshTokenExpirationTime = authenticationServiceImpl.getRefreshTokenDurationMs();
            // Gửi refresh token về phía client thông qua HttpOnly Cookie (bảo mật hơn localStorage)
            ResponseCookie responseCookie = ResponseCookie.from("refresh_token", newRefreshToken)
                    .httpOnly(true) // Không thể đọc bằng JavaScript → tăng bảo mật
                    .secure(true) // Chỉ sử dụng qua HTTPS
                    .path("/") // Cookie có hiệu lực toàn bộ hệ thống
                    .maxAge(refreshTokenExpirationTime) // Thời gian sống của cookie
                    .build();

            // Trả về login response kèm cookie
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .body(refreshTokenResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@Valid @RequestBody VerifyUserRequest verifyUserRequest) {
        try {
            authenticationServiceImpl.verifyUser(verifyUserRequest);
            return ResponseEntity.ok("Account verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
            authenticationServiceImpl.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        authenticationServiceImpl.forgotPassword(forgotPasswordRequest);
        return ResponseEntity.ok("Verification code sent");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody OtpRequest otpRequest) {
        return ResponseEntity.ok(authenticationServiceImpl.verifyOtp(otpRequest));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest, @RequestHeader(name = "Authorization") String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
        String token = authorization.substring(7);
        authenticationServiceImpl.resetPassword(resetPasswordRequest, token);
        return ResponseEntity.ok("Password reset successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        String email = SecurityUtils.getCurrentUser();
        if (email.isEmpty() || "anonymousUser".equals(email)) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "Invalid or anonymous user");
        }

        return ResponseEntity.ok(authenticationServiceImpl.getCurrentUser(email));
    }
}
