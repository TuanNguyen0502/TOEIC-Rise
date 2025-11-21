package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.dtos.requests.report.QuestionReportRequest;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("learnerQuestionReportController")
@RequestMapping("/learner/question-reports")
@RequiredArgsConstructor
public class QuestionReportController {
    private final IQuestionReportService questionReportService;

    @PostMapping("")
    public String createReport(@Valid @RequestBody QuestionReportRequest questionReportRequest) {
        String email = SecurityUtils.getCurrentUser();
        questionReportService.createReport(email, questionReportRequest);
        return "Report created successfully";
    }
}
