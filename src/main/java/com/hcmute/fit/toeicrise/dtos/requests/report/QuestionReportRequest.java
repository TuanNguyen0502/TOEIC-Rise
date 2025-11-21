package com.hcmute.fit.toeicrise.dtos.requests.report;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.models.enums.EQuestionReportReason;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QuestionReportRequest {
    @NotNull(message = MessageConstant.QUESTION_ID_NOT_NULL)
    private Long questionId;

    @NotEmpty(message = MessageConstant.QUESTION_REPORT_REASONS_NOT_EMPTY)
    private List<@NotNull(message = MessageConstant.QUESTION_REPORT_REASONS_NOT_EMPTY) EQuestionReportReason> reasons;

    private String description;
}
