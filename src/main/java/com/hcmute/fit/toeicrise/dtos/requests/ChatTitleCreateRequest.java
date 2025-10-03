package com.hcmute.fit.toeicrise.dtos.requests;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ChatTitleCreateRequest {
    @NotBlank(message = MessageConstant.CHAT_CONVERSATION_ID_NOT_BLANK)
    @Pattern(regexp = Constant.CHAT_CONVERSATION_ID_PATTERN, message = MessageConstant.CHAT_CONVERSATION_ID_INVALID)
    private String conversationId;

    @NotBlank(message = MessageConstant.CHAT_TITLE_NOT_BLANK)
    @Pattern(regexp = Constant.CHAT_TITLE_PATTERN, message = MessageConstant.CHAT_TITLE_INVALID)
    private String newTitle;
}
