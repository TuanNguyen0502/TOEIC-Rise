package com.hcmute.fit.toeicrise.dtos.responses;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionResponse {
    private Long id;
    private Integer position;
    private String questionGroupPosition;
    private String part;
    private String createdAt;
    private String updatedAt;
    private List<String> tags;
}
