package com.hcmute.fit.toeicrise.dtos.requests.chatbot;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ChatRequest {
    @NotBlank(message = MessageConstant.CHAT_CONVERSATION_ID_NOT_BLANK)
    @Pattern(regexp = Constant.CHAT_CONVERSATION_ID_PATTERN, message = MessageConstant.CHAT_CONVERSATION_ID_INVALID)
    private String conversationId;

    @NotBlank(message = MessageConstant.CHAT_MESSAGE_NOT_BLANK)
    private String message;

    private MultipartFile image;
}
