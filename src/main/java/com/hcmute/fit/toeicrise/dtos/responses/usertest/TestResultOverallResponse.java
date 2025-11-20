package com.hcmute.fit.toeicrise.dtos.responses.usertest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestResultOverallResponse {
    private Long userTestId;
    private int totalQuestions;
    private int correctAnswers;
    private int score;
    private int timeSpent; // in seconds
}
