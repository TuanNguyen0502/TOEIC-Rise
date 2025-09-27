package com.hcmute.fit.toeicrise.dtos.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SystemPromptDetailResponse {
    private Long id;
    private String content;
    private Integer version;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;
}
