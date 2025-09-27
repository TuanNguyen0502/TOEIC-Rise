package com.hcmute.fit.toeicrise.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SystemPromptCreateRequest {
    @NotBlank(message = "Content must not be blank")
    private String content;

    @NotBlank(message = "Version must not be blank")
    private Integer version;
}
