package com.hcmute.fit.toeicrise.dtos.responses.learner.writing;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LearnerWritingTestDetailResponse {
    private Long id;
    private String testName;
    private List<LearnerWritingPartDetailResponse> partResponses;
}
