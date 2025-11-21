package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.models.enums.EDays;
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
    private final IUserTestService userTestService;

    @GetMapping("")
    public ResponseEntity<?> analysis(@RequestParam(value = "days") EDays days){
        String email = SecurityUtils.getCurrentUser();
        return ResponseEntity.ok(userTestService.getAnalysisResult(email, days));
    }
}
