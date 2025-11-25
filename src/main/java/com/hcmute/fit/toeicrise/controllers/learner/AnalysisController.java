package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.services.interfaces.IAnalysisService;
import com.hcmute.fit.toeicrise.services.interfaces.IUserTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/learner/analysis")
@RequiredArgsConstructor
public class AnalysisController {
    private final IAnalysisService analysisService;
    private final IUserTestService userTestService;

    @GetMapping("")
    public ResponseEntity<?> analysis(@RequestParam(value = "days") EDays days){
        return ResponseEntity.ok(userTestService.getAnalysisResult(SecurityUtils.getCurrentUser(), days));
    }

    @GetMapping("/result")
    public ResponseEntity<?> getAnalysis(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(analysisService.getAllTestHistory(page, size, SecurityUtils.getCurrentUser()));
    }

    @GetMapping("/full-test")
    public ResponseEntity<?> getFullTest(@RequestParam(defaultValue = "5") int size){
        return ResponseEntity.ok(userTestService.getFullTestResult(SecurityUtils.getCurrentUser(), size));
    }
}
