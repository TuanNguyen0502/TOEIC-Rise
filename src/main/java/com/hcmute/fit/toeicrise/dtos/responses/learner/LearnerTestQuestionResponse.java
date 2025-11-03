package com.hcmute.fit.toeicrise.dtos.responses.learner;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LearnerTestQuestionResponse {
    private Long id;
    private String position;
    private String content;
    private List<String> options;
}