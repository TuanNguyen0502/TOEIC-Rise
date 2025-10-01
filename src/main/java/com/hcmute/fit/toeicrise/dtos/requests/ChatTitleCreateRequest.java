package com.hcmute.fit.toeicrise.dtos.requests;

import lombok.Data;

@Data
public class ChatTitleCreateRequest {
    private Long userId;
    private String conversationId;
    private String title;
}
