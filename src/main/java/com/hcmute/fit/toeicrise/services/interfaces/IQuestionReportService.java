package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.report.QuestionReportRequest;

public interface IQuestionReportService {
    void createReport(String email, QuestionReportRequest questionReportRequest);
}
