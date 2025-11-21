package com.hcmute.fit.toeicrise.controllers.staff;

import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.dtos.responses.report.QuestionReportDetailResponse;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("staffQuestionReportController")
@RequestMapping("/staff/question-reports")
@RequiredArgsConstructor
public class QuestionReportController {
    private final IQuestionReportService questionReportService;

    @GetMapping("/{id}")
    public QuestionReportDetailResponse getQuestionReportDetail(@PathVariable Long id) {
        String currentUserEmail = SecurityUtils.getCurrentUser();
        return questionReportService.getReportDetail(currentUserEmail, id);
    }
}
