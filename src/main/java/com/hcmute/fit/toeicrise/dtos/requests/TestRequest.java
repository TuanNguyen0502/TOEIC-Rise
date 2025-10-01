package com.hcmute.fit.toeicrise.dtos.requests;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestRequest {
    @Pattern(regexp = Constant.TEST_NAME_PATTERN, message = MessageConstant.INVALID_TEST_NAME)
    @NotBlank(message = MessageConstant.TEST_NAME_NOT_BLANK)
    private String testName;

    @NotNull(message = "Test set id is required")
    @Min(value = 1, message = "Test set id must be greater than 0")
    private Long testSetId;
}
