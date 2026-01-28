package com.hcmute.fit.toeicrise.dtos.requests.usertest;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScoreAccumulator {
    private int correctAnswers;
    private int listeningCorrectAnswers;
    private int readingCorrectAnswers;
    private int listeningTotal;
    private int readingTotal;
}
