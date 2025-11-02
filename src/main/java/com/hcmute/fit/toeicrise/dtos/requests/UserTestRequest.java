package com.hcmute.fit.toeicrise.dtos.requests;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserTestRequest {
    @NotNull(message = MessageConstant.TEST_ID_NOT_NULL)
    private Long testId;

    @NotNull(message = MessageConstant.TIME_SPENT_NOT_NULL)
    private int timeSpent; // in seconds

    // List of parts the user has completed
    // parts != null is practice mode
    // parts == null is full test mode
    private List<String> parts; // Part 1, Part 2, ...

    @NotNull(message = MessageConstant.ANSWERS_NOT_NULL)
    private List<UserAnswerRequest> answers;
}
