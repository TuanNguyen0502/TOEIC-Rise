package com.hcmute.fit.toeicrise.dtos.responses.learner;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LearnerTestPartResponse {
    private Long id;
    private String partName;
    private List<LearnerTestQuestionGroupResponse> questionGroups;
}