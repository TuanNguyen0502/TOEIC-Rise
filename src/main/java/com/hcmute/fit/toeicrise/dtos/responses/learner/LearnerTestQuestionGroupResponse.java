package com.hcmute.fit.toeicrise.dtos.responses.learner;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LearnerTestQuestionGroupResponse {
    private Long id;
    private String audioUrl;
    private String imageUrl;
    private String passage;
    private Long position;
    List<LearnerTestQuestionResponse> questions;
}