package com.hcmute.fit.toeicrise.dtos.requests.minitest;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAnswerMiniTestRequest {
    @NotNull(message = MessageConstant.QUESTION_ID_NOT_NULL)
    private Long questionId;

    private String answer;
}