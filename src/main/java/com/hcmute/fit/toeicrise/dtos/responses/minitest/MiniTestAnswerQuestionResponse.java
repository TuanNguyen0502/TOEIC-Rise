package com.hcmute.fit.toeicrise.dtos.responses.minitest;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MiniTestAnswerQuestionResponse {
    private Long id;
    private Long position;
    private Long index;
    private String content;
    private List<String> options;
    private String userAnswer;
    private String correctOption;
    private String explanation;
    private Boolean isCorrect;
    private List<String> tags;
}
