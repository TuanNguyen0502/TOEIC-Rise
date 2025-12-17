package com.hcmute.fit.toeicrise.dtos.responses.statistic;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestModeInsightResponse {
    private double fullTest;
    private double pratice;
}
