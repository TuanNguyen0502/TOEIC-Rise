package com.hcmute.fit.toeicrise.dtos.responses.test;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionGroupResponse {
    private Long id;
    private String audioUrl;
    private String imageUrl;
    private String passage;
    private String transcript;
    private Integer position;
    private List<QuestionResponse> questions;
}
