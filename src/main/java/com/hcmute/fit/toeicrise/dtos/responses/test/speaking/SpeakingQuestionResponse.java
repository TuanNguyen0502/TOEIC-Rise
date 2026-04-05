package com.hcmute.fit.toeicrise.dtos.responses.test.speaking;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpeakingQuestionResponse {
    private Long id;
    private Integer position;
    private String content;
}