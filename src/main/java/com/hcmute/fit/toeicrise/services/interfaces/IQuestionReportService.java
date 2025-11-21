package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.report.QuestionReportRequest;
import com.hcmute.fit.toeicrise.dtos.responses.report.QuestionReportDetailResponse;

public interface IQuestionReportService {
    void createReport(String email, QuestionReportRequest questionReportRequest);

    QuestionReportDetailResponse getReportDetail(String email, Long reportId);
}
