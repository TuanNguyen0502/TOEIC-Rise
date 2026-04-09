package com.hcmute.fit.toeicrise.dtos.responses.usertest.writing;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WritingTestResultOverallResponse {
    private Long userTestId;
    private int totalQuestions;
    private int totalAnswers;
    private int timeSpent; // in seconds
}
