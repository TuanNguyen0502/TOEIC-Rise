package com.hcmute.fit.toeicrise.dtos.requests.learningpath;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.models.enums.ETestType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLearningPathRequest {
    @NotNull(message = MessageConstant.LEARNING_PATH_ID_NOT_NULL)
    private Long learningPathId;

    @NotNull(message = MessageConstant.TEST_TYPE_NOT_NULL)
    private ETestType testType;
}
