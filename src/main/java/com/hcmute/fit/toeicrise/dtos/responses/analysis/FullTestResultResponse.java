package com.hcmute.fit.toeicrise.dtos.responses.analysis;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FullTestResultResponse {
    private int numberOfTests;
    private double averageScore;
    private int highestScore;
    List<ExamTypeFullTestResponse> examTypeFullTestResponses;
}
