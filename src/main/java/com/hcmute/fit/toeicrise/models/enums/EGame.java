package com.hcmute.fit.toeicrise.models.enums;

import lombok.Getter;

@Getter
public enum EGame {
    WORD_MATCHING("Word Matching", "Nối từ với nghĩa"),
    MULTIPLE_CHOICE("Multile Choice", "Trắc nghiệm"),
    VI_TO_EN_PRACTICE("Vi-En Translation", "Dịch Việt - Anh"),
    AI_SENTENCE_PRACTICE("AI Sentence Practice", "Luyện câu với AI");

    private final String name;
    private final String description;

    private EGame(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
