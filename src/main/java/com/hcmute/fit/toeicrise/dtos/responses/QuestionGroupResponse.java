package com.hcmute.fit.toeicrise.dtos.responses;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionGroupResponse {
    private Long id;
    private String audioUrl;
    private String imageUrl;
    private String passage;
    private Integer position;
    private List<QuestionResponse> questions;
}
