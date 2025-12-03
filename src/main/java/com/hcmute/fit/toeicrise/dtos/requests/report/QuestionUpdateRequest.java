package com.hcmute.fit.toeicrise.dtos.requests.report;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionUpdateRequest {
    private String content;
    private List<String> options;

    @NotBlank(message = MessageConstant.QUESTION_CORRECT_OPTION_NOT_BLANK)
    private String correctOption;

    @NotBlank(message = MessageConstant.QUESTION_EXPLANATION_NOT_BLANK)
    private String explanation;
}
