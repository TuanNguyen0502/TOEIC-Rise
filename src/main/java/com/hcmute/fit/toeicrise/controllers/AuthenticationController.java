package com.hcmute.fit.toeicrise.controllers;

import com.hcmute.fit.toeicrise.dtos.requests.*;
import com.hcmute.fit.toeicrise.dtos.responses.LoginResponse;
import com.hcmute.fit.toeicrise.dtos.responses.RefreshTokenResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Account;
import com.hcmute.fit.toeicrise.models.entities.RefreshToken;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.services.interfaces.IAuthenticationService;
import com.hcmute.fit.toeicrise.services.interfaces.IRefreshTokenService;
import com.hcmute.fit.toeicrise.services.interfaces.IUserService;
import com.hcmute.fit.toeicrise.services.interfaces.IJwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final IJwtService IJwtService;
    private final IAuthenticationService authenticationServiceImpl;
    private final IRefreshTokenService refreshTokenService;
    private final IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<Account> register(@Valid @RequestBody RegisterRequest registerRequest) {
        Account registeredUser = authenticationServiceImpl.register(registerRequest);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginRequest loginRequest) {
        Account authenticatedUser = authenticationServiceImpl.authenticate(loginRequest);
        User user = userService.findByAccountId(authenticatedUser.getId());
        String accessToken = IJwtService.generateToken(authenticatedUser);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authenticatedUser.getEmail());

        return ResponseEntity.ok(LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expirationTime(IJwtService.getExpirationTime())
                .userId(user.getId())
                .email(authenticatedUser.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().getName())
                .build());
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody String refreshToken) {
        try {
            RefreshToken token = refreshTokenService.verifyExpiration(
                    refreshTokenService.findByToken(refreshToken)
            );
            String accessToken = IJwtService.generateToken(token.getAccount());
            return ResponseEntity.ok(RefreshTokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(token.getToken())
                    .expirationTime(IJwtService.getExpirationTime())
                    .build());
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
    public ResponseEntity<?> verifyOtp(@RequestBody OtpRequest otpRequest) {
        return ResponseEntity.ok(authenticationServiceImpl.verifyOtp(otpRequest));
    }

    @PostMapping("/reset-password")
    private ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest, @RequestHeader(name = "Authorization") String authorization) {
        if (authorization == null||!authorization.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
        String token = authorization.substring(7);
        authenticationServiceImpl.resetPassword(resetPasswordRequest, token);
        return ResponseEntity.ok("Password reset successfully");
    }
}
