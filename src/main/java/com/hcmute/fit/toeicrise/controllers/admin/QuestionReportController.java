package com.hcmute.fit.toeicrise.controllers.admin;

import com.hcmute.fit.toeicrise.models.enums.EQuestionReportStatus;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("adminQuestionReportController")
@RequestMapping("/admin/question-reports")
@RequiredArgsConstructor
public class QuestionReportController {
    private final IQuestionReportService questionReportService;

    @GetMapping("")
    public ResponseEntity<?> getAllQuestionReports(@RequestParam(required = false) EQuestionReportStatus questionReportStatus,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(questionReportService.getAllReports(questionReportStatus, page, size));
    }
}
