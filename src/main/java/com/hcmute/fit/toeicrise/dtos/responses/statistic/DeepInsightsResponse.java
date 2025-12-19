package com.hcmute.fit.toeicrise.dtos.responses.statistic;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeepInsightsResponse {
    private TestModeInsightResponse testMode;
    private RegSourceInsightResponse regSource;
    private ScoreDistInsightResponse scoreDist;
}
