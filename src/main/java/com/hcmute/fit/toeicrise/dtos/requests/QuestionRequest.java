package com.hcmute.fit.toeicrise.dtos.requests;

import io.swagger.v3.core.util.Json;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionRequest {
    private Integer questionGroupId;
    private Integer position;
    private String content;
    private Json options;
    private String correctAnswer;
    private String explanation;
}