package com.hcmute.fit.toeicrise.controllers;

import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.dtos.requests.ProfileUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.UserChangePasswordRequest;
import com.hcmute.fit.toeicrise.services.interfaces.IAuthenticationService;
import com.hcmute.fit.toeicrise.services.interfaces.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final IAuthenticationService authenticationService;
    private final IUserService userService;

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody UserChangePasswordRequest userChangePasswordRequest) {
        authenticationService.changePassword(userChangePasswordRequest, SecurityUtils.getCurrentUser());
        return ResponseEntity.ok("Password updated successfully");
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        String email = SecurityUtils.getCurrentUser();
        return ResponseEntity.ok(userService.getUserProfileByEmail(email));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@Valid @ModelAttribute ProfileUpdateRequest profileUpdateRequest) {
        String email = SecurityUtils.getCurrentUser();
        userService.updateUserProfile(email, profileUpdateRequest);
        return ResponseEntity.ok("Profile updated successfully");
    }
}