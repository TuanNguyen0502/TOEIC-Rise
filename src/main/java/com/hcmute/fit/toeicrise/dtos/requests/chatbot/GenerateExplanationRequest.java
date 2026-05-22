package com.hcmute.fit.toeicrise.dtos.requests.chatbot;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateExplanationRequest {
    private String passage;
    private String transcript;
    private String content;
    private List<String> options;

    @NotBlank(message = MessageConstant.QUESTION_CORRECT_OPTION_NOT_BLANK)
    private String correctOption;
}
