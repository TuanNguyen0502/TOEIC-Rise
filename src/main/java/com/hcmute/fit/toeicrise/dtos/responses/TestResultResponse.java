package com.hcmute.fit.toeicrise.dtos.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestResultResponse {
    private Long userTestId;
    private int totalQuestions;
    private int correctAnswers;
    private int score;
    private int timeSpent; // in seconds
}
