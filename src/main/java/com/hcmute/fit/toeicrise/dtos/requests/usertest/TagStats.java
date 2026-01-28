package com.hcmute.fit.toeicrise.dtos.requests.usertest;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagStats {
    private int correct = 0;
    private int wrong = 0;

    public void add(int correctDelta, int wrongDelta) {
        this.correct += correctDelta;
        this.wrong += wrongDelta;
    }
}
