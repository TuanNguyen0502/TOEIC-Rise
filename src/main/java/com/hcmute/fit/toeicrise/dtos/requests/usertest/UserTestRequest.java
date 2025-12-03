package com.hcmute.fit.toeicrise.dtos.requests.usertest;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.dtos.requests.useranswer.UserAnswerRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTestRequest {
    @NotNull(message = MessageConstant.TEST_ID_NOT_NULL)
    private Long testId;

    @Min(value = 1, message = MessageConstant.TIME_SPENT_MIN)
    private int timeSpent; // in seconds

    /**
     * A list of specific test parts (e.g., "Part 1", "Part 2") that the user has completed.
     * <p>
     * This field determines the operating mode:
     * <ul>
     * <li>If {@code parts != null}: The system is running in **Practice Mode**, focusing on the listed parts.</li>
     * <li>If {@code parts == null}: The system is running in **Full Test Mode**, where all parts are expected.</li>
     * </ul>
     * The list contains identifiers for the completed sections.
     */
    private List<String> parts;

    @NotEmpty(message = MessageConstant.ANSWERS_NOT_EMPTY)
    private List<@Valid UserAnswerRequest> answers;
}
