package com.hcmute.fit.toeicrise.dtos.responses.test.writing;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WritingQuestionGroupResponse {
    private Long id;
    private String imageUrl;
    private String passage;
    private Integer position;
    private List<WritingQuestionResponse> questions;
}
