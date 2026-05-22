package com.hcmute.fit.toeicrise.dtos.requests.learningpath;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
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
public class ItemRequest {
    @NotNull(message = MessageConstant.LESSON_ID_NOT_NULL)
    private Long lessonId;

    @NotNull(message = MessageConstant.LESSON_ORDER_INDEX_NOT_NULL)
    @Min(value = 1, message = MessageConstant.LESSON_ORDER_INDEX_MIN)
    private Integer orderIndex;
}
