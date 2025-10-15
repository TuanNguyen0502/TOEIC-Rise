package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.dtos.requests.UserChangePasswordRequest;
import com.hcmute.fit.toeicrise.services.interfaces.IAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/learner")
@RequiredArgsConstructor
public class LearnerController {
    private final IAuthenticationService authenticationService;
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody UserChangePasswordRequest userChangePasswordRequest) {
        authenticationService.changePassword(userChangePasswordRequest, SecurityUtils.getCurrentUser());
        return ResponseEntity.ok("Password updated successfully");
    }
}