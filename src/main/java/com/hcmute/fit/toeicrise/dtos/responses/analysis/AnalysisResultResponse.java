package com.hcmute.fit.toeicrise.dtos.responses.analysis;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResultResponse {
    private int numberOfTests;
    private long totalTimes;
    List<ExamTypeStatsResponse> examList;
}
