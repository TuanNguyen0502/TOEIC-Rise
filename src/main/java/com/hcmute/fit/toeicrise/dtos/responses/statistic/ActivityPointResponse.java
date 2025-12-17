package com.hcmute.fit.toeicrise.dtos.responses.statistic;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityPointResponse {
    private LocalDate date;
    private Long submissions;
}
