package com.hcmute.fit.toeicrise.dtos.requests;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SystemPromptUpdateRequest {
    @NotBlank(message = MessageConstant.NOT_BLANK_SYSTEM_PROMPT_CONTENT)
    @Pattern(regexp = Constant.SYSTEM_PROMPT_CONTENT_PATTERN, message = MessageConstant.INVALID_SYSTEM_PROMPT_CONTENT)
    private String content;

    @NotNull(message = MessageConstant.NOT_NULL_SYSTEM_PROMPT_IS_ACTIVE)
    private Boolean isActive;
}
