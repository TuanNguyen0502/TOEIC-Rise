package com.hcmute.fit.toeicrise.dtos.responses.learner;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LearnerAnswerResponse {
    private Long learnerAnswerId;
    private Long questionId;
    private Long position;
    private String content;
    private List<String> options;
    private String userAnswer;
    private String userAnswerText;
    private String userAnswerAudioUrl;
    private String feedback;
    private String correctOption;
    private String explanation;
    private Boolean isCorrect;
}
