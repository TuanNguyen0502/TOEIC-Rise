package com.hcmute.fit.toeicrise.dtos.responses.learner;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RedoWrongQuestionResponse {
    private Long id;
    private Long position;
    private String content;
    private List<String> options;
    private String correctOption;
    private String explanation;
}
