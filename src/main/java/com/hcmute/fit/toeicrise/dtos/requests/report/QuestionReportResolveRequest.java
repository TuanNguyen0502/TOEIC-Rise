package com.hcmute.fit.toeicrise.dtos.requests.report;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionGroupUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionRequest;
import com.hcmute.fit.toeicrise.models.enums.EQuestionReportStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionReportResolveRequest {
    @NotNull(message = MessageConstant.QUESTION_REPORT_STATUS_NOT_NULL)
    @NotBlank(message = MessageConstant.QUESTION_REPORT_STATUS_NOT_BLANK)
    private EQuestionReportStatus status;

    @Valid
    private QuestionRequest questionUpdate;

    @Valid
    private QuestionGroupUpdateRequest questionGroupUpdate;

    @NotNull(message = MessageConstant.QUESTION_REPORT_RESOLVED_NOTE_NOT_NULL)
    @NotBlank(message = MessageConstant.QUESTION_REPORT_RESOLVED_NOTE_NOT_BLANK)
    private String resolvedNote;
}
