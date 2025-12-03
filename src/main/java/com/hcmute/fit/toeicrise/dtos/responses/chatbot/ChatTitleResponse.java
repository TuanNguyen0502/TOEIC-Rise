package com.hcmute.fit.toeicrise.dtos.responses.chatbot;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatTitleResponse {
    private String conversationId;
    private String title;
}
