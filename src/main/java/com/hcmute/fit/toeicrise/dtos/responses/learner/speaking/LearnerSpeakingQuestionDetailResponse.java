package com.hcmute.fit.toeicrise.dtos.responses.learner.speaking;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LearnerSpeakingQuestionDetailResponse {
    private Long id;
    private Integer position;
    private String content;
}
