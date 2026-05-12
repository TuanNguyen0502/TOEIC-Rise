package com.hcmute.fit.toeicrise.models.enums;

public enum ELessonLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED;

    public ELessonLevel getNextLevel() {
        int nextOrdinal = this.ordinal() + 1;
        ELessonLevel[] levels = ELessonLevel.values();
        if (nextOrdinal >= levels.length) {
            return this;
        }
        return levels[nextOrdinal];
    }
}
