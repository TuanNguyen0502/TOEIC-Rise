package com.hcmute.fit.toeicrise.dtos.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatTitleResponse {
    private String conversationId;
    private String title;
}
