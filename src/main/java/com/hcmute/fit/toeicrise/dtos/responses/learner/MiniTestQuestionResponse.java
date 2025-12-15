package com.hcmute.fit.toeicrise.dtos.responses.learner;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MiniTestQuestionResponse {
    private Long id;
    private Long position;
    private Long newPosition;
    private String content;
    private List<String> options;
    private List<String> tags;
}
