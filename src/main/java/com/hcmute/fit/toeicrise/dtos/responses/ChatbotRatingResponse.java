package com.hcmute.fit.toeicrise.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatbotRatingResponse {
    private Long id;
    private String conversationTitle;
    private String message;
    private String rating;
    private String createdAt;
}
