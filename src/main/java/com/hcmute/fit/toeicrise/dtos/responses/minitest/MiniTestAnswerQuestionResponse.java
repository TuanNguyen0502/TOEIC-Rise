package com.hcmute.fit.toeicrise.dtos.responses.minitest;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MiniTestAnswerQuestionResponse {
    private Long questionId;
    private int position;
    private String content;
    private List<String> options;
    private String userAnswer;
    private String correctOption;
    private String explanation;
    private Boolean isCorrect;
}
