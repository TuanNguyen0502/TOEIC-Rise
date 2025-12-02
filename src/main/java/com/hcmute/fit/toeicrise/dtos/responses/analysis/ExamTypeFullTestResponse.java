package com.hcmute.fit.toeicrise.dtos.responses.analysis;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExamTypeFullTestResponse {
    private long id;
    private String name;
    private String createdAt;
    private int listeningScore;
    private int readingScore;
    private int totalScore;
}
