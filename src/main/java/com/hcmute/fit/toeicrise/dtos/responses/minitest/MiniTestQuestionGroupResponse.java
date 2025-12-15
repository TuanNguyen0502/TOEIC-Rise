package com.hcmute.fit.toeicrise.dtos.responses.minitest;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MiniTestQuestionGroupResponse {
    private Long id;
    private int index;
    private String audioUrl;
    private String imageUrl;
    private String passage;
    List<Object> questions;
}
