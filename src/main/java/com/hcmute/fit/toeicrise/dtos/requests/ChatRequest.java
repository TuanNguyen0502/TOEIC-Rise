package com.hcmute.fit.toeicrise.dtos.requests;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {
    private String conversationId;

    @NotBlank(message = MessageConstant.CHAT_MESSAGE_NOT_BLANK)
    private String message;
}
