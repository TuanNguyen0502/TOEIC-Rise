package com.hcmute.fit.toeicrise.dtos.requests;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.validators.annotations.ValidQuestionByPart;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ValidQuestionByPart
public class QuestionRequest {
    @NotNull(message = MessageConstant.QUESTION_ID_NOT_NULL)
    @NotBlank(message = MessageConstant.QUESTION_ID_NOT_BLANK)
    private Long id;
    @NotNull(message = MessageConstant.QUESTION_GROUP_ID_NOT_NULL)
    @NotBlank(message = MessageConstant.QUESTION_GROUP_ID_NOT_BLANK)
    private Long questionGroupId;
    private String content;
    private Map<String, String> options;
    @NotBlank(message = MessageConstant.CORRECT_OPTION_NOT_BLANK)
    @NotNull(message = MessageConstant.CORRECT_OPTION_NOT_NULL)
    private String correctOption;
    @NotBlank(message = MessageConstant.EXPLAIN_NOT_BLANK)
    @NotNull(message = MessageConstant.EXPLAIN_NOT_NULL)
    private String explanation;
}