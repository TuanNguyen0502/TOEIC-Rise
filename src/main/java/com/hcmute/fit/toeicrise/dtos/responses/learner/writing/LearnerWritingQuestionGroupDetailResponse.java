package com.hcmute.fit.toeicrise.dtos.responses.learner.writing;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LearnerWritingQuestionGroupDetailResponse {
    private Long id;
    private Integer position;
    private String passage;
    private String imageUrl;
    private List<LearnerWritingQuestionDetailResponse> questionDetailResponses;
}
