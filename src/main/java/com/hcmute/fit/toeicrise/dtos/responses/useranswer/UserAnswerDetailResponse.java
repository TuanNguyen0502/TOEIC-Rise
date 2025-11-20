package com.hcmute.fit.toeicrise.dtos.responses.useranswer;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserAnswerDetailResponse {
    private String userAnswer;
    private int position;
    private List<String> tags;
    private String audioUrl;
    private String imageUrl;
    private String passage;
    private String transcript;
    private String questionContent;
    private List<String> options;
    private String correctOption;
    private String explanation;
}
