package com.hcmute.fit.toeicrise.controllers;

import com.hcmute.fit.toeicrise.dtos.requests.LoginRequest;
import com.hcmute.fit.toeicrise.dtos.requests.RegisterRequest;
import com.hcmute.fit.toeicrise.dtos.requests.VerifyUserRequest;
import com.hcmute.fit.toeicrise.dtos.responses.LoginResponse;
import com.hcmute.fit.toeicrise.dtos.responses.RefreshTokenResponse;
import com.hcmute.fit.toeicrise.models.entities.Account;
import com.hcmute.fit.toeicrise.models.entities.RefreshToken;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.services.impl.AuthenticationServiceImpl;
import com.hcmute.fit.toeicrise.services.impl.JwtService;
import com.hcmute.fit.toeicrise.services.interfaces.IRefreshTokenService;
import com.hcmute.fit.toeicrise.services.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationServiceImpl authenticationServiceImpl;
    private final IRefreshTokenService refreshTokenService;
    private final IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody RegisterRequest registerRequest) {
        Account registeredUser = authenticationServiceImpl.register(registerRequest);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginRequest loginRequest) {
        Account authenticatedUser = authenticationServiceImpl.authenticate(loginRequest);
        User user = userService.findByAccountId(authenticatedUser.getId());
        String accessToken = jwtService.generateToken(authenticatedUser);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authenticatedUser.getEmail());

        return ResponseEntity.ok(LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expirationTime(jwtService.getExpirationTime())
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
            String accessToken = jwtService.generateToken(token.getAccount());
            return ResponseEntity.ok(RefreshTokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(token.getToken())
                    .expirationTime(jwtService.getExpirationTime())
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserRequest verifyUserRequest) {
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
}
