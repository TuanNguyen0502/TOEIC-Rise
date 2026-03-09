package com.hcmute.fit.toeicrise.models.enums;

import lombok.Getter;

import java.time.Duration;

@Getter
public enum ELevel {
    LEVEL_0("Level 0", Duration.ofHours(6)),
    LEVEL_1("Level 1", Duration.ofDays(1)),
    LEVEL_2("Level 2", Duration.ofDays(3)),
    LEVEL_3("Level 3", Duration.ofDays(7)),
    LEVEL_4("Level 4", Duration.ofDays(14)),
    LEVEL_5("Level 5", Duration.ofDays(30)),
    LEVEL_6("Level 6", Duration.ofDays(90));

    private final String name;
    private final Duration duration;

    ELevel(String name, Duration duration) {
        this.name = name;
        this.duration = duration;
    }

    public ELevel next(boolean isCorrect) {
        int index = this.ordinal() + (isCorrect ? 1 : -1);
        index = Math.max(0, Math.min(index, ELevel.values().length - 1));
        return ELevel.values()[index];
    }
}
