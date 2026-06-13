package com.hcmute.fit.toeicrise.dtos.requests.chatbot;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestingSystemPromptSpeakingAssessmentRequest {
    private MultipartFile audio;

    @NotNull(message = MessageConstant.QUESTION_ID_NOT_NULL)
    private Long questionId;

    @NotBlank(message = MessageConstant.SYSTEM_PROMPT_CONTENT_NOT_BLANK)
    @Pattern(regexp = Constant.SYSTEM_PROMPT_CONTENT_PATTERN, message = MessageConstant.SYSTEM_PROMPT_CONTENT_INVALID)
    private String systemPromptContent;
}
