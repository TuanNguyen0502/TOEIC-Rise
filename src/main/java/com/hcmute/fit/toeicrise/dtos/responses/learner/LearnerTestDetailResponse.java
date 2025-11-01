package com.hcmute.fit.toeicrise.dtos.responses.learner;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LearnerTestDetailResponse {
    private Long testId;
    private String testName;
    private Long numberOfLearnedTests;
    private List<LearnerPartResponse> learnerPartResponses;
}