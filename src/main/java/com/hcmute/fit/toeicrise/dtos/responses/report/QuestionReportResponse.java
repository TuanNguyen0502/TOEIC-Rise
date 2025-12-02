package com.hcmute.fit.toeicrise.dtos.responses.report;

import com.hcmute.fit.toeicrise.models.enums.EQuestionReportReason;
import com.hcmute.fit.toeicrise.models.enums.EQuestionReportStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionReportResponse {
    private Long id;
    private String testName;
    private String reporterName;
    private String resolverName;
    private EQuestionReportStatus status;
    private List<EQuestionReportReason> reasons;
}
