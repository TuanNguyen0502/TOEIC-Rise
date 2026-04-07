package com.hcmute.fit.toeicrise.dtos.responses.learner.writing;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LearnerWritingPartDetailResponse {
    private Long id;
    private String partName;
    private List<LearnerWritingQuestionGroupDetailResponse> questionGroupResponses;
}
