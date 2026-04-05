package com.hcmute.fit.toeicrise.dtos.responses.test.speaking;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SpeakingQuestionGroupResponse {
    private Long id;
    private String imageUrl;
    private String passage;
    private Integer position;
    private List<SpeakingQuestionResponse> questions;
}
