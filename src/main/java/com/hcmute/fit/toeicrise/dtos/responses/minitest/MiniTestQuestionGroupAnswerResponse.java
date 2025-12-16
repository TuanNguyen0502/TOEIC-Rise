package com.hcmute.fit.toeicrise.dtos.responses.minitest;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MiniTestQuestionGroupAnswerResponse {
    private Long id;
    private int index;
    private int position;
    private String transcript;
    private String audioUrl;
    private String imageUrl;
    private String passage;
    List<MiniTestAnswerQuestionResponse> questions;
}
