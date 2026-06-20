package com.hcmute.fit.toeicrise.dtos.requests.learningpath;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonReorderRequest {
    @NotEmpty(message = MessageConstant.LESSONS_NOT_EMPTY)
    private List<ItemRequest> items;

    @NotBlank(message = MessageConstant.LEARNING_PATH_SLUG_NOT_BLANK)
    private String learningPathSlug;
}

