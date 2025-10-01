package com.hcmute.fit.toeicrise.dtos.requests;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestUpdateRequest {
    @Pattern(regexp = Constant.TEST_NAME_PATTERN, message = MessageConstant.TEST_NAME_INVALID)
    @NotBlank(message = MessageConstant.TEST_NAME_NOT_BLANK)
    private String name;

    @NotNull(message = MessageConstant.TEST_STATUS_NOT_NULL)
    private ETestStatus status;
}
