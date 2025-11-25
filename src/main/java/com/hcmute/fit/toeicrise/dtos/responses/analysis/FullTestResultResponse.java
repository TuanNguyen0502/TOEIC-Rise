package com.hcmute.fit.toeicrise.dtos.responses.analysis;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FullTestResultResponse {
    private int averageScore;
    private int highestScore;
    private int averageListeningScore;
    private int averageReadingScore;
    private int maxListeningScore;
    private int maxReadingScore;
    List<ExamTypeFullTestResponse> examTypeFullTestResponses;
}
