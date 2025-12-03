package com.hcmute.fit.toeicrise.dtos.responses.chatbot;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemPromptDetailResponse {
    private Long id;
    private String content;
    private Integer version;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;
}
