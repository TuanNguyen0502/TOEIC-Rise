package com.hcmute.fit.toeicrise.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatbotRatingDetailResponse {
    private Long id;
    private String userEmail;
    private String conversationTitle;
    private String messageId;
    private String message;
    private String rating;
    private String createdAt;
    private List<ChatbotResponse> chatbotResponses;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatbotResponse {
        private String content;
        private String messageType;
        private String rating;
    }
}
