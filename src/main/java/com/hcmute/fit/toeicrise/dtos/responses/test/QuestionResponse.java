package com.hcmute.fit.toeicrise.dtos.responses.test;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionResponse {
    private Long id;
    private Long position;
    private String content;
    private List<String> options;
    private String correctOption;
    private String explanation;
    private List<String> tags;
}