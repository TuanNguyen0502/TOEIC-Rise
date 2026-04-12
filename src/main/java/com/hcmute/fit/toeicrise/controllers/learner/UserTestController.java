package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.dtos.requests.usertest.UserTestRequest;
import com.hcmute.fit.toeicrise.dtos.requests.usertest.speaking.SpeakingTestSubmissionRequest;
import com.hcmute.fit.toeicrise.dtos.requests.usertest.writing.WritingTestSubmissionRequest;
import com.hcmute.fit.toeicrise.dtos.responses.learner.speaking.LearnerSpeakingTestDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.writing.LearnerWritingTestDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.usertest.TestResultResponse;
import com.hcmute.fit.toeicrise.dtos.responses.useranswer.UserAnswerOverallResponse;
import com.hcmute.fit.toeicrise.dtos.responses.usertest.speakingwriting.SpeakingWritingTestResultOverallResponse;
import com.hcmute.fit.toeicrise.dtos.responses.usertest.speakingwriting.SpeakingWritingTestResultResponse;
import com.hcmute.fit.toeicrise.services.interfaces.ITestService;
import com.hcmute.fit.toeicrise.services.interfaces.IUserTestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/learner/user-tests")
@RequiredArgsConstructor
public class UserTestController {
    private final ITestService testService;
    private final IUserTestService userTestService;

    @GetMapping("/{userTestId}")
    public ResponseEntity<?> getUserTestResultById(@PathVariable Long userTestId) {
        String email = SecurityUtils.getCurrentUser();
        TestResultResponse result = userTestService.getUserTestResultById(email, userTestId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/writing/{userTestId}")
    public ResponseEntity<SpeakingWritingTestResultResponse> getWritingTestResultById(@PathVariable Long userTestId) {
        String email = SecurityUtils.getCurrentUser();
        return ResponseEntity.ok(userTestService.getWritingTestResultById(email, userTestId));
    }

    @GetMapping("/answers-overall/{userTestId}")
    public ResponseEntity<?> getUserAnswersOverallGroupedByPart(@PathVariable Long userTestId) {
        String email = SecurityUtils.getCurrentUser();
        Map<String, List<UserAnswerOverallResponse>> result = userTestService.getUserAnswersGroupedByPart(email, userTestId);
        return ResponseEntity.ok(result);
    }

    @PostMapping()
    public ResponseEntity<?> submitTest(@Valid @RequestBody UserTestRequest request) {
        String email = SecurityUtils.getCurrentUser();
        return ResponseEntity.ok(userTestService.calculateAndSaveUserTestResult(email, request));
    }

    @PostMapping("/submit-writing-test")
    public ResponseEntity<SpeakingWritingTestResultOverallResponse> submitWritingTest(@Valid @RequestBody WritingTestSubmissionRequest request) {
        String email = SecurityUtils.getCurrentUser();
        return ResponseEntity.ok(userTestService.submitWritingTest(email, request));
    }

    @PostMapping(value = "/submit-speaking-test", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SpeakingWritingTestResultOverallResponse> submitSpeakingTest(@Valid @ModelAttribute SpeakingTestSubmissionRequest request) {
        String email = SecurityUtils.getCurrentUser();
        return ResponseEntity.ok(userTestService.submitSpeakingTest(email, request));
    }

    @GetMapping("/view-histories/{id}")
    public ResponseEntity<?> getTestHistory(@PathVariable Long id) {
        return ResponseEntity.ok(userTestService.allLearnerTestHistories(id, SecurityUtils.getCurrentUser()));
    }

    @GetMapping("/exam/{id}")
    public ResponseEntity<?> getTestByParts(@PathVariable Long id, @RequestParam("parts") List<Long> parts) {
        return ResponseEntity.ok(userTestService.getTestByIdAndParts(id, parts));
    }

    @GetMapping("/speaking-exam/{id}")
    public ResponseEntity<LearnerSpeakingTestDetailResponse> getSpeakingExamTestById(@PathVariable Long id, @RequestParam("parts") List<Long> parts) {
        return ResponseEntity.ok(testService.getSpeakingTestDetailResponseForExam(id, parts));
    }

    @GetMapping("/writing-exam/{id}")
    public ResponseEntity<LearnerWritingTestDetailResponse> getWritingExamTestById(@PathVariable Long id, @RequestParam("parts") List<Long> parts) {
        return ResponseEntity.ok(testService.getWritingTestDetailResponseForExam(id, parts));
    }

    @GetMapping("/detail/{userTestId}")
    public ResponseEntity<?> getTestDetail(@PathVariable Long userTestId) {
        return ResponseEntity.ok(userTestService.getUserTestDetail(userTestId, SecurityUtils.getCurrentUser()));
    }

    @GetMapping("/{userTestId}/wrong-answer")
    public ResponseEntity<?> getWrongAnswer(@PathVariable Long userTestId) {
        return ResponseEntity.ok(userTestService.getLearnerWrongAnswer(userTestId, SecurityUtils.getCurrentUser()));
    }

    @PostMapping("/{userTestId}/wrong-answer")
    public ResponseEntity<?> submitWrongAnswer(@PathVariable Long userTestId, @Valid @RequestBody UserTestRequest request) {
        return ResponseEntity.ok(userTestService.getResultAfterSubmitWrongAnswer(userTestId, SecurityUtils.getCurrentUser(), request));
    }

    @GetMapping("{userTestId}/do-wrong-answer")
    public ResponseEntity<?> getDoWrongAnswer(@PathVariable Long userTestId) {
        return ResponseEntity.ok(userTestService.getQuestionsAndCorrectAnswersWrongAnswer(userTestId, SecurityUtils.getCurrentUser()));
    }
}
