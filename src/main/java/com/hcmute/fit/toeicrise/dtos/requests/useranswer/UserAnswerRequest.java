package com.hcmute.fit.toeicrise.dtos.requests.useranswer;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAnswerRequest {
    @NotNull(message = MessageConstant.QUESTION_ID_NOT_NULL)
    private Long questionId;

    @NotNull(message = MessageConstant.QUESTION_GROUP_ID_NOT_NULL)
    private Long questionGroupId;

    private String answer;
}
