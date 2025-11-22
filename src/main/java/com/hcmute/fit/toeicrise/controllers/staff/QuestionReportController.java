package com.hcmute.fit.toeicrise.controllers.staff;

import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.dtos.responses.report.QuestionReportDetailResponse;
import com.hcmute.fit.toeicrise.models.enums.EQuestionReportStatus;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("staffQuestionReportController")
@RequestMapping("/staff/question-reports")
@RequiredArgsConstructor
public class QuestionReportController {
    private final IQuestionReportService questionReportService;

    @GetMapping("")
    public ResponseEntity<?> getAllQuestionReports(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(questionReportService.getAllReports(page, size));
    }

    @GetMapping("/{id}")
    public QuestionReportDetailResponse getQuestionReportDetail(@PathVariable Long id) {
        String currentUserEmail = SecurityUtils.getCurrentUser();
        return questionReportService.getReportDetail(currentUserEmail, id);
    }
}
