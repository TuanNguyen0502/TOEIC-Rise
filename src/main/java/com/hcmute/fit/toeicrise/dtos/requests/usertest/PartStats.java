package com.hcmute.fit.toeicrise.dtos.requests.usertest;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartStats {
    @Builder.Default
    private int correct = 0;
    @Builder.Default
    private int wrong = 0;

    public void add(int correctDelta, int wrongDelta) {
        this.correct += correctDelta;
        this.wrong += wrongDelta;
    }
}
