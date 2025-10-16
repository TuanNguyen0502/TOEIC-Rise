package com.hcmute.fit.toeicrise.dtos.requests;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TitleRequest {
    @NotBlank(message = MessageConstant.CHAT_CONVERSATION_ID_NOT_BLANK)
    @Pattern(regexp = Constant.CHAT_CONVERSATION_ID_PATTERN, message = MessageConstant.CHAT_CONVERSATION_ID_INVALID)
    private String conversationId;

    @NotBlank(message = MessageConstant.CHAT_MESSAGE_NOT_BLANK)
    private String message;
}
