package com.hcmute.fit.toeicrise.dtos.responses.flashcard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FlashcardForPopupResponse {
    private Long id;
    private String name;
    private String description;
    private int itemCount;
}
