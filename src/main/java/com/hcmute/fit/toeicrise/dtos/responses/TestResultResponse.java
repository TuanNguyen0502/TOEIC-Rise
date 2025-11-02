package com.hcmute.fit.toeicrise.dtos.responses;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TestResultResponse {
    private Long testId;
    private Long userTestId;
    private String testName;
    private List<String> parts;
    private int totalQuestions;
    private int correctAnswers;
    private double correctPercent;
    private int timeSpent; // in seconds
    // Fields for full test mode
    private int score;
    private int listeningScore;
    private int listeningCorrectAnswers;
    private int readingScore;
    private int readingCorrectAnswers;
}
