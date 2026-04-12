package com.hcmute.fit.toeicrise.dtos.requests.learningpath;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
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
public class LearningPathUpdateRequest {
    @NotNull(message = MessageConstant.LEARNING_PATH_NAME_NOT_NULL)
    @NotBlank(message = MessageConstant.LEARNING_PATH_NAME_NOT_BLANK)
    @Size(max = 255, message = MessageConstant.LEARNING_PATH_NAME_MAX)
    private String name;

    @Size(max = 5000, message = MessageConstant.LEARNING_PATH_DESCRIPTION_MAX)
    private String description;

    private Boolean isActive;
}
