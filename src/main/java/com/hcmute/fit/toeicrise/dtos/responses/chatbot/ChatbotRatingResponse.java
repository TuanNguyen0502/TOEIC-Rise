package com.hcmute.fit.toeicrise.dtos.responses.chatbot;

import lombok.*;

@Setter
@Getter
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
