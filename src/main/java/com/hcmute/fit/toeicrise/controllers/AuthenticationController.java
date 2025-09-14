package com.hcmute.fit.toeicrise.controllers;

import com.hcmute.fit.toeicrise.dtos.requests.*;
import com.hcmute.fit.toeicrise.dtos.responses.LoginResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.services.interfaces.IAuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        return ResponseEntity.ok(authenticationServiceImpl.login(loginRequest));
    }

    @GetMapping("/login/google")
    public ResponseEntity<String > loginWithGoogleAuth(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
        return ResponseEntity.ok("Redirecting ..");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody String refreshToken) {
        try {
            return ResponseEntity.ok(authenticationServiceImpl.refreshToken(refreshToken));
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
    private ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest, @RequestHeader(name = "Authorization") String authorization) {
        if (authorization == null||!authorization.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
        String token = authorization.substring(7);
        authenticationServiceImpl.resetPassword(resetPasswordRequest, token);
        return ResponseEntity.ok("Password reset successfully");
    }
}
