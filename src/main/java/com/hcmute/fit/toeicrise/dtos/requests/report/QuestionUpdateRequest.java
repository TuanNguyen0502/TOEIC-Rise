package com.hcmute.fit.toeicrise.dtos.requests.report;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionUpdateRequest {
    private String content;
    private List<String> options;

    @NotBlank(message = MessageConstant.QUESTION_CORRECT_OPTION_NOT_BLANK)
    @NotNull(message = MessageConstant.QUESTION_CORRECT_OPTION_NOT_NULL)
    private String correctOption;

    @NotNull(message = MessageConstant.QUESTION_EXPLANATION_NOT_NULL)
    @NotBlank(message = MessageConstant.QUESTION_EXPLANATION_NOT_BLANK)
    private String explanation;
}
