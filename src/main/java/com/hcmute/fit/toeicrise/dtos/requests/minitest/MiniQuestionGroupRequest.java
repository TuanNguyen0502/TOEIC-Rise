package com.hcmute.fit.toeicrise.dtos.requests.minitest;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MiniQuestionGroupRequest {
    @NotNull(message = MessageConstant.QUESTION_GROUP_ID_NOT_NULL)
    Long questionGroupId;

    @NotNull(message = MessageConstant.USER_ANSWER_NOT_NULL)
    @NotEmpty(message = MessageConstant.USER_ANSWER_NOT_EMPTY)
    List<UserAnswerMiniTestRequest> userAnswerRequests;
}
