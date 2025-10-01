package com.hcmute.fit.toeicrise.dtos.requests;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatTitleUpdateRequest {
    @NotBlank(message = MessageConstant.CHAT_CONVERSATION_ID_NOT_BLANK)
    private String conversationId;

    @NotBlank(message = MessageConstant.CHAT_TITLE_NOT_BLANK)
    private String newTitle;
}
