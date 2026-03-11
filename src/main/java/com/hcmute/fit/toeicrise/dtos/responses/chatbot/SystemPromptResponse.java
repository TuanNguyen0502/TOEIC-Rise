package com.hcmute.fit.toeicrise.dtos.responses.chatbot;

import com.hcmute.fit.toeicrise.models.enums.ESystemPromptFeatureType;
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
    private ESystemPromptFeatureType featureType;
    private String content;
    private Integer version;
    private Boolean isActive;
    private String updatedAt;
}
