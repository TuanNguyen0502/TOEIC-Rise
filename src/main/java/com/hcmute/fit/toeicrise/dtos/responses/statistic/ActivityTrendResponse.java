package com.hcmute.fit.toeicrise.dtos.responses.statistic;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityTrendResponse {
    private Long totalSubmissions;
    private List<ActivityPointResponse> points;
}
