package com.hcmute.fit.toeicrise.dtos.responses.learner.writing;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LearnerWritingQuestionDetailResponse {
    private Long id;
    private Integer position;
}
