package com.hcmute.fit.toeicrise.dtos.requests.report;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionGroupUpdateRequest;
import com.hcmute.fit.toeicrise.models.enums.EQuestionReportStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionReportResolveRequest {
    private EQuestionReportStatus status;

    @Valid
    private QuestionUpdateRequest questionUpdate;

    @Valid
    private QuestionGroupUpdateRequest questionGroupUpdate;

    @NotBlank(message = MessageConstant.QUESTION_REPORT_RESOLVED_NOTE_NOT_BLANK)
    private String resolvedNote;
}
