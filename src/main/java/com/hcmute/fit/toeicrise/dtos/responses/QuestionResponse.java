package com.hcmute.fit.toeicrise.dtos.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionResponse {
    private Long id;
    private String position;
    private String content;
    private String correctOption;
}
