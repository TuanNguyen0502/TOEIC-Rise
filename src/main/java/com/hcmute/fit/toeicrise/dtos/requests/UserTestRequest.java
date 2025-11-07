package com.hcmute.fit.toeicrise.dtos.requests;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserTestRequest {
    @NotNull(message = MessageConstant.TEST_ID_NOT_NULL)
    private Long testId;

    @Min(value = 1, message = MessageConstant.TIME_SPENT_MIN)
    private int timeSpent; // in seconds

    // List of parts the user has completed
    // parts != null is practice mode
    // parts == null is full test mode
    private List<String> parts; // Part 1, Part 2, ...

    @NotEmpty(message = MessageConstant.ANSWERS_NOT_EMPTY)
    private List<@Valid UserAnswerRequest> answers;
}
