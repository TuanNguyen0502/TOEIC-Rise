package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.models.enums.ETestSetType;
import com.hcmute.fit.toeicrise.services.interfaces.ITestSetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("learnerWritingTestSetController")
@RequestMapping("/writing-test-sets")
@RequiredArgsConstructor
public class WritingTestSetController {
    private final ITestSetService testSetService;

    @GetMapping("")
    public ResponseEntity<?> getAllTestSets() {
        return ResponseEntity.ok(testSetService.getAllTestSetsByType(ETestSetType.WRITING));
    }
}