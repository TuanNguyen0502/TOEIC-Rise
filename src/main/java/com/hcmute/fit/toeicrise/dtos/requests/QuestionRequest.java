package com.hcmute.fit.toeicrise.dtos.requests;

import com.hcmute.fit.toeicrise.validators.annotations.ValidQuestionByPart;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ValidQuestionByPart
public class QuestionRequest {
    @NotNull(message = "QuestionId is required")
    private Long id;
    @NotNull(message = "Question group id is required")
    private Long questionGroupId;
    private String content;
    private Map<String, String> options;
    @NotBlank(message = "Correct answer is required")
    private String correctOption;
    @NotBlank(message = "Explanation is required")
    private String explanation;
}