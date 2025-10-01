package com.hcmute.fit.toeicrise.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemPromptResponse {
    private Long id;
    private String content;
    private Integer version;
    private Boolean isActive;
    private String updatedAt;
}
