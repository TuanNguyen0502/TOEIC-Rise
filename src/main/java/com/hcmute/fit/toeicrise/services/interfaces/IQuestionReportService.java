package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.report.QuestionReportRequest;
import com.hcmute.fit.toeicrise.dtos.requests.report.QuestionReportResolveRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.report.QuestionReportDetailResponse;
import com.hcmute.fit.toeicrise.models.enums.EQuestionReportStatus;
import jakarta.transaction.Transactional;

public interface IQuestionReportService {
    void createReport(String email, QuestionReportRequest questionReportRequest);

    QuestionReportDetailResponse getReportDetail(String email, Long reportId);

    PageResponse getAllReports(EQuestionReportStatus status, int page, int size);

    @Transactional
    void resolveReport(String email, Long reportId, QuestionReportResolveRequest request);
}
