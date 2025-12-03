package com.hcmute.fit.toeicrise.dtos.responses.chatbot;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatbotResponse {
    private String messageId;
    private String conversationId;
    private String content;
    private String messageType;
}
