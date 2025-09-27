package com.hcmute.fit.toeicrise.dtos.requests;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SystemPromptCreateRequest {
    @NotBlank(message = MessageConstant.NOT_BLANK_SYSTEM_PROMPT_CONTENT)
    @Pattern(regexp = Constant.SYSTEM_PROMPT_CONTENT_PATTERN, message = MessageConstant.INVALID_SYSTEM_PROMPT_CONTENT)
    private String content;
}
