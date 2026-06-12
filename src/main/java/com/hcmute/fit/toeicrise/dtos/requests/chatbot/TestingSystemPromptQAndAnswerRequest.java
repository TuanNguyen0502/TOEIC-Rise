package com.hcmute.fit.toeicrise.dtos.requests.chatbot;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestingSystemPromptQAndAnswerRequest {
    @Pattern(regexp = Constant.CHAT_CONVERSATION_ID_PATTERN, message = MessageConstant.CHAT_CONVERSATION_ID_INVALID)
    private String conversationId;

    @NotBlank(message = MessageConstant.CHAT_MESSAGE_NOT_BLANK)
    private String message;

    @NotNull(message = MessageConstant.QUESTION_ID_NOT_NULL)
    private Long questionId;

    @NotBlank(message = MessageConstant.SYSTEM_PROMPT_CONTENT_NOT_BLANK)
    @Pattern(regexp = Constant.SYSTEM_PROMPT_CONTENT_PATTERN, message = MessageConstant.SYSTEM_PROMPT_CONTENT_INVALID)
    private String systemPromptContent;
}
