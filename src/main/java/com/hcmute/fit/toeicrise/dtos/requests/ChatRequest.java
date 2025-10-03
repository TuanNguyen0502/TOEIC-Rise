package com.hcmute.fit.toeicrise.dtos.requests;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ChatRequest {
    @NotBlank(message = MessageConstant.CHAT_CONVERSATION_ID_NOT_BLANK)
    private String conversationId;

    @NotBlank(message = MessageConstant.CHAT_MESSAGE_NOT_BLANK)
    private String message;

    private MultipartFile image;
}
