package com.hcmute.fit.toeicrise.dtos.responses.learner;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LearnerAnswerResponse {
    private Long id;
    private Long position;
    private String content;
    private List<String> options;
    private String userAnswer;
    private String correctOption;
    private String explanation;
    private Boolean isCorrect;
}
