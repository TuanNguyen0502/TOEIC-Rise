package com.hcmute.fit.toeicrise.dtos.requests.learningpath;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLessonProgressUpsertRequest {
    @NotNull(message = MessageConstant.LESSON_ID_NOT_NULL)
    private Long lessonId;

    @NotNull(message = MessageConstant.USER_PROGRESS_PERCENT_NOT_NULL)
    @Min(value = 0, message = MessageConstant.USER_PROGRESS_PERCENT_MIN)
    @Max(value = 100, message = MessageConstant.USER_PROGRESS_PERCENT_MAX)
    private Double progressPercentage;

    @NotNull(message = MessageConstant.USER_PROGRESS_LAST_WATCHED_TIME_MS_NOT_NULL)
    @Min(value = 0, message = MessageConstant.USER_PROGRESS_LAST_WATCHED_TIME_MS_MIN)
    private Long lastWatchedTimeMs;
}
