package com.hcmute.fit.toeicrise.dtos.requests.test;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.validators.annotations.NotBlankOrEmptyOptional;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PageRequest {
    @Min(value = 0, message = MessageConstant.PAGE_MIN)
    @Max(value = 100, message = MessageConstant.PAGE_MAX)
    private int page;

    @Min(value = 10, message = MessageConstant.SIZE_PAGE_MIN)
    @Max(value = 50, message = MessageConstant.SIZE_PAGE_MAX)
    private int size;

    private List<Long> sort;

    @NotBlankOrEmptyOptional(regexp = Constant.TEST_NAME_PATTERN, max = 100, message = MessageConstant.INVALID_INPUT_DATA)
    private String name;
}