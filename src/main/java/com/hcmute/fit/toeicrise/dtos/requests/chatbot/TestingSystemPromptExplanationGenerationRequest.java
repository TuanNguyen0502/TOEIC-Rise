package com.hcmute.fit.toeicrise.dtos.requests.chatbot;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestingSystemPromptExplanationGenerationRequest {
    @NotBlank(message = MessageConstant.PART_NAME_NOT_BLANK)
    private String partName;
}
