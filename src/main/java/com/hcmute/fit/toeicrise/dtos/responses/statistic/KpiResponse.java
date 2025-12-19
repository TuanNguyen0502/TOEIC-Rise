package com.hcmute.fit.toeicrise.dtos.responses.statistic;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KpiResponse {
    private Long value;
    private double growthPercentage;
}
