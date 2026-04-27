package com.hcmute.fit.toeicrise.dtos.responses.learner.speaking;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LearnerSpeakingTestDetailResponse {
    private Long id;
    private String testName;
    private List<LearnerSpeakingPartDetailResponse> partResponses;
}
