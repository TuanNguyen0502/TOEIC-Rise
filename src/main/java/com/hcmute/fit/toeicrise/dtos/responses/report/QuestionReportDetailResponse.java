package com.hcmute.fit.toeicrise.dtos.responses.report;

import com.hcmute.fit.toeicrise.models.enums.EQuestionReportReason;
import com.hcmute.fit.toeicrise.models.enums.EQuestionReportStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionReportDetailResponse {
    private Long questionReportId;
    private Long questionId;
    private String questionContent;
    private List<String> questionOptions;
    private String questionCorrectOption;
    private String questionExplanation;
    private List<String> questionTags;
    private Long questionGroupId;
    private String questionGroupAudioUrl;
    private String questionGroupImageUrl;
    private String questionGroupPassage;
    private String questionGroupTranscript;
    private String partName;
    private Long reporterId;
    private String reporterFullName;
    private String reporterEmail;
    private Long resolverId;
    private String resolverFullName;
    private String resolverEmail;
    private List<EQuestionReportReason> reasons;
    private String description;
    private EQuestionReportStatus status;
    private String resolvedNote;
}
