package com.hcmute.fit.toeicrise.dtos.responses.chatbot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotResponse {
    private String messageId;
    private String conversationId;
    private String content;
    private String messageType;
}
