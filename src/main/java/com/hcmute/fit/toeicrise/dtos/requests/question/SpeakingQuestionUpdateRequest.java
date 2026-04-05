package com.hcmute.fit.toeicrise.dtos.requests.question;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.validators.annotations.ValidQuestionByPart;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ValidQuestionByPart
public class SpeakingQuestionUpdateRequest {
    @NotNull(message = MessageConstant.QUESTION_ID_NOT_NULL)
    private Long id;

    @NotNull(message = MessageConstant.QUESTION_GROUP_ID_NOT_NULL)
    private Long questionGroupId;

    private String content;
}