package com.hcmute.fit.toeicrise.dtos.requests;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestUpdateRequest {
    @Pattern(regexp = Constant.TEST_NAME_PATTERN, message = MessageConstant.INVALID_TEST_NAME)
    private String name;
    private ETestStatus status;
}
