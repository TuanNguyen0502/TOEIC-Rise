package com.hcmute.fit.toeicrise.dtos.responses.statistic;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScoreDistInsightResponse {
    private double brand0_200;
    private double brand200_450;
    private double brand450_750;
    private double brand750_990;

    public double sum(){
        return brand0_200 + brand200_450 + brand450_750 + brand750_990;
    }
}
