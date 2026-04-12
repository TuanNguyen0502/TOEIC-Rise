package com.hcmute.fit.toeicrise.dtos.requests.learningpath;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.models.enums.ELessonLevel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonCreateRequest {
    @NotNull(message = MessageConstant.LESSON_TITLE_NOT_NULL)
    @NotBlank(message = MessageConstant.LESSON_TITLE_NOT_BLANK)
    @Size(max = 255, message = MessageConstant.LESSON_TITLE_MAX)
    private String title;

    @Size(max = 500, message = MessageConstant.LESSON_VIDEO_URL_MAX)
    private String videoUrl;

    @Size(max = 255, message = MessageConstant.LESSON_TOPIC_MAX)
    @NotBlank(message = MessageConstant.LESSON_TOPIC_NOT_BLANK)
    @NotNull(message = MessageConstant.LESSON_TOPIC_NOT_NULL)
    private String topic;

    @NotNull(message = MessageConstant.LESSON_LEVEL_NOT_NULL)
    private ELessonLevel level;

    private String content;
    private Boolean isActive;

    @NotNull(message = MessageConstant.LESSON_ORDER_INDEX_NOT_NULL)
    @Min(value = 1, message = MessageConstant.LESSON_ORDER_INDEX_MIN)
    private Integer orderIndex;
}
