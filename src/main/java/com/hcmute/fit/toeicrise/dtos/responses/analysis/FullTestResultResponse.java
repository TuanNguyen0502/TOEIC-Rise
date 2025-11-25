package com.hcmute.fit.toeicrise.dtos.responses.analysis;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FullTestResultResponse {
    private double averageScore;
    private int highestScore;
    private double averageListeningScore;
    private double averageReadingScore;
    private int maxListeningScore;
    private int maxReadingScore;
    List<ExamTypeFullTestResponse> examTypeFullTestResponses;
}
