package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.dtos.requests.UserTestRequest;
import com.hcmute.fit.toeicrise.dtos.responses.TestResultOverallResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestResultResponse;
import com.hcmute.fit.toeicrise.dtos.responses.UserAnswerOverallResponse;
import com.hcmute.fit.toeicrise.services.interfaces.IUserTestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/learner/user-tests")
@RequiredArgsConstructor
public class UserTestController {
    private final IUserTestService userTestService;

    @GetMapping("/{userTestId}")
    public ResponseEntity<TestResultResponse> getUserTestResultById(@PathVariable Long userTestId) {
        String email = SecurityUtils.getCurrentUser();
        TestResultResponse result = userTestService.getUserTestResultById(email, userTestId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/answers-overall/{userTestId}")
    public ResponseEntity<?> getUserAnswersOverallGroupedByPart(@PathVariable Long userTestId) {
        String email = SecurityUtils.getCurrentUser();
        Map<String, List<UserAnswerOverallResponse>> result = userTestService.getUserAnswersGroupedByPart(email, userTestId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("")
    public ResponseEntity<TestResultOverallResponse> submitTest(@Valid @RequestBody UserTestRequest request) {
        String email = SecurityUtils.getCurrentUser();
        TestResultOverallResponse result = userTestService.calculateAndSaveUserTestResult(email, request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/view-histories/{id}")
    public ResponseEntity<?> getTestHistory(@PathVariable Long id) {
        return ResponseEntity.ok(userTestService.allLearnerTestHistories(id, SecurityUtils.getCurrentUser()));
    }

    @GetMapping(value = "/exam/{id}")
    public ResponseEntity<?> getTestByParts(@PathVariable Long id, @RequestParam("parts") List<Long> parts) {
        return ResponseEntity.ok(userTestService.getTestByIdAndParts(id, parts));
    }

    @GetMapping("/detail/{userTestId}")
    public ResponseEntity<?> getTestDetail(@PathVariable Long userTestId) {
        return ResponseEntity.ok(userTestService.getUserTestDetail(userTestId));
    }
}
