package com.hcmute.fit.toeicrise.dtos.responses.question;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionForTestingResponse {
    private Long id;
    private Integer position;
    private String content;
    private List<String> options;
    private String correctOption;
    private String imageUrl;
    private String passage;
    private String transcript;
    private String partName;
}
