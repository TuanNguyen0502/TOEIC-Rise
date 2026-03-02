package com.hcmute.fit.toeicrise.dtos.responses.usertest;

import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestPartsResponse;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RetestResultOverallResponse {
    private int totalQuestions;
    private int correctAnswers;
    private int timeSpent;
    private LearnerTestPartsResponse learnerTestPartsResponse;
}
