package com.hcmute.fit.toeicrise.dtos.responses.test;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LearnerTestResponse {
    private Long id;
    private String testName;
    private String testSetName;
    private Long numberOfLearnerTests;
}