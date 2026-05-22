package com.hcmute.fit.toeicrise.dtos.responses.learner.speaking;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LearnerSpeakingPartDetailResponse {
    private Long id;
    private String partName;
    private List<LearnerSpeakingQuestionGroupDetailResponse> questionGroupResponses;
}
