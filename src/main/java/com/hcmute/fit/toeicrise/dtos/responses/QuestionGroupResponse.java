package com.hcmute.fit.toeicrise.dtos.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionGroupResponse {
    private Long id;
    private Integer position;
    private String partName;
    private Integer numberOfQuestions;
    private String createdAt;
    private String updatedAt;
}
