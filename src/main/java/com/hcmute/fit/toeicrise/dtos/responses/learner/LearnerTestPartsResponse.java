package com.hcmute.fit.toeicrise.dtos.responses.learner;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LearnerTestPartsResponse {
    private Long id;
    private String testName;
    private List<LearnerTestPartResponse> partResponses;
}