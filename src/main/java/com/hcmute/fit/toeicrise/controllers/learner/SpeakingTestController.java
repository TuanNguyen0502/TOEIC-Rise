package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.dtos.requests.test.PageRequest;
import com.hcmute.fit.toeicrise.models.enums.ETestType;
import com.hcmute.fit.toeicrise.services.interfaces.ITestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("LearnerSpeakingTestController")
@RequestMapping("/speaking-tests")
@RequiredArgsConstructor
public class SpeakingTestController {
    private final ITestService testService;

    @GetMapping("")
    public ResponseEntity<?> getAllTests(@Valid @ModelAttribute PageRequest pageRequest) {
        return ResponseEntity.ok(testService.searchTestsByTypeAndName(ETestType.SPEAKING, pageRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTestById(@PathVariable Long id) {
        return ResponseEntity.ok(testService.getLearnerTestDetailById(id));
    }
}