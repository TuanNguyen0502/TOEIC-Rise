package com.hcmute.fit.toeicrise.dtos.responses.test.writing;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WritingQuestionResponse {
    private Long id;
    private Integer position;
}