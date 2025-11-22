package com.hcmute.fit.toeicrise.controllers.staff;

import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.dtos.requests.report.QuestionReportResolveRequest;
import com.hcmute.fit.toeicrise.dtos.responses.report.QuestionReportDetailResponse;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuestionReport(@PathVariable Long id, @Valid @RequestBody QuestionReportResolveRequest request) {
        String currentUserEmail = SecurityUtils.getCurrentUser();
        questionReportService.resolveReport(currentUserEmail, id, request);
        return ResponseEntity.ok().build();
    }
}
