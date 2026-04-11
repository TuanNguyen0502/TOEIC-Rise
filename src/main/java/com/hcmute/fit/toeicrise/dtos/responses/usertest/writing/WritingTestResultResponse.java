package com.hcmute.fit.toeicrise.dtos.responses.usertest.writing;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WritingTestResultResponse {
    private Long testId;
    private Long userTestId;
    private String testName;
    private List<String> parts;
    private int totalQuestions;
    private int totalAnswers;
    private int timeSpent; // in seconds
}
