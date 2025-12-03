package com.hcmute.fit.toeicrise.dtos.responses.flashcard;

import com.hcmute.fit.toeicrise.models.enums.EFlashcardAccessType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FlashcardResponse {
    private Long id;
    private String authorFullName;
    private String name;
    private EFlashcardAccessType accessType;
    private int itemCount;
    private int favouriteCount;
}
