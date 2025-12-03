package com.hcmute.fit.toeicrise.dtos.requests.chatbot;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.dtos.responses.analysis.AnalysisResultResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatAnalysisRequest {
    @NotBlank(message = MessageConstant.CHAT_MESSAGE_NOT_BLANK)
    AnalysisResultResponse analysisResult;
}
