package com.hcmute.fit.toeicrise.dtos.requests;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SystemPromptCreateRequest {
    @NotBlank(message = MessageConstant.SYSTEM_PROMPT_CONTENT_NOT_BLANK)
    @NotNull(message = MessageConstant.SYSTEM_PROMPT_CONTENT_NOT_NULL)
    @Pattern(regexp = Constant.SYSTEM_PROMPT_CONTENT_PATTERN, message = MessageConstant.SYSTEM_PROMPT_CONTENT_INVALID)
    private String content;
}
