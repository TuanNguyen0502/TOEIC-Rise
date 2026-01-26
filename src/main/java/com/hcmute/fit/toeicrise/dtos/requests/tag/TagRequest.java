package com.hcmute.fit.toeicrise.dtos.requests.tag;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagRequest {
    @NotNull(message = MessageConstant.TAG_NAME_NOT_NULL)
    @NotBlank(message = MessageConstant.TAG_NAME_NOT_BLANK)
    @Pattern(regexp = Constant.TAG_NAME_PATTERN, message = MessageConstant.TAG_NAME_INVALID)
    private String name;
}
