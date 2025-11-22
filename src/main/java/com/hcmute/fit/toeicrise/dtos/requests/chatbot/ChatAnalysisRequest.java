package com.hcmute.fit.toeicrise.dtos.requests.chatbot;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.dtos.responses.analysis.AnalysisResultResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatAnalysisRequest {
    @Pattern(regexp = Constant.CHAT_CONVERSATION_ID_PATTERN, message = MessageConstant.CHAT_CONVERSATION_ID_INVALID)
    private String conversationId;

    @NotBlank(message = MessageConstant.CHAT_MESSAGE_NOT_BLANK)
    AnalysisResultResponse analysisResult;
}
