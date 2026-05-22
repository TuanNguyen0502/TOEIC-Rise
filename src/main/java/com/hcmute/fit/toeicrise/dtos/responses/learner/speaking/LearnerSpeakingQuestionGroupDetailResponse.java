package com.hcmute.fit.toeicrise.dtos.responses.learner.speaking;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LearnerSpeakingQuestionGroupDetailResponse {
    private Long id;
    private Integer position;
    private String passage;
    private String imageUrl;
    private List<LearnerSpeakingQuestionDetailResponse> questionDetailResponses;
}
