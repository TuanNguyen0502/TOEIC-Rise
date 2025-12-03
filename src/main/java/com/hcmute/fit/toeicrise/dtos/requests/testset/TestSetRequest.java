package com.hcmute.fit.toeicrise.dtos.requests.testset;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestSetRequest {
    @Pattern(regexp = Constant.TEST_SET_NAME_OR_TEST_NAME_PATTERN, message = MessageConstant.INVALID_TEST_SET)
    private String testName;
}