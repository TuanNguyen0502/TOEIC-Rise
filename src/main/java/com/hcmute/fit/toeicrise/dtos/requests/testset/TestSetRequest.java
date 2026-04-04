package com.hcmute.fit.toeicrise.dtos.requests.testset;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.models.enums.ETestSetType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestSetRequest {
    @Pattern(regexp = Constant.TEST_SET_NAME_PATTERN, message = MessageConstant.INVALID_TEST_SET)
    private String testName;

    @NotNull(message = MessageConstant.TEST_SET_TYPE_NOT_NULL)
    private ETestSetType testSetType;
}