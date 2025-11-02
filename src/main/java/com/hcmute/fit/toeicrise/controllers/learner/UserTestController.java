package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.dtos.requests.UserTestRequest;
import com.hcmute.fit.toeicrise.dtos.responses.TestResultOverallResponse;
import com.hcmute.fit.toeicrise.services.interfaces.IUserTestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/learner/user-tests")
@RequiredArgsConstructor
public class UserTestController {
    private final IUserTestService userTestService;

    @PostMapping("")
    public ResponseEntity<TestResultOverallResponse> submitTest(@Valid @RequestBody UserTestRequest request) {
        String email = SecurityUtils.getCurrentUser();
        TestResultOverallResponse result = userTestService.calculateAndSaveUserTestResult(email, request);
        return ResponseEntity.ok(result);
    }
}
