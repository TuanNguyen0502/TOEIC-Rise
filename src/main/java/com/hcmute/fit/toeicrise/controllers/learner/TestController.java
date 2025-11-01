package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.dtos.requests.PageRequest;
import com.hcmute.fit.toeicrise.services.interfaces.ITestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("LearnerTestController")
@RequestMapping("/tests")
@RequiredArgsConstructor
public class TestController {
    private final ITestService testService;

    @GetMapping("")
    public ResponseEntity<?> getAllTests(@Valid @ModelAttribute PageRequest pageRequest) {
        return ResponseEntity.ok(testService.searchTestsByName(pageRequest));
    }

    @GetMapping("/view-histories/{id}")
    public ResponseEntity<?> getTestHistory(@PathVariable Long id) {
        return ResponseEntity.ok(testService.allLearnerTestHistories(id, SecurityUtils.getCurrentUser()));
    }
 
    @GetMapping("/{id}")
    public ResponseEntity<?> getTestById(@PathVariable Long id) {
        return ResponseEntity.ok(testService.getLearnerTestDetailById(id));
    }
}