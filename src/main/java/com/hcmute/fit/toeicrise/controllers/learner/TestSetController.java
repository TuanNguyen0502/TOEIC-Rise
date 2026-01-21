package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.services.interfaces.ITestSetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("learnerTestSetController")
@RequestMapping("/test-sets")
@RequiredArgsConstructor
public class TestSetController {
    private final ITestSetService testSetService;

    @GetMapping("")
    public ResponseEntity<?> getAllTestSets() {
        return ResponseEntity.ok(testSetService.getAllTestSets());
    }
}