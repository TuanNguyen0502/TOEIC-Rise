package com.hcmute.fit.toeicrise.dtos.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAnswerOverallResponse {
    private Long userAnswerId;
    private int position;
    private String correctAnswer;
    private String userAnswer;
}
