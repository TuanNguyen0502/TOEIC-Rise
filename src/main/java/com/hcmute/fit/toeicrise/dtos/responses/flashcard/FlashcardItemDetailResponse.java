package com.hcmute.fit.toeicrise.dtos.responses.flashcard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FlashcardItemDetailResponse {
    private Long id;
    private String vocabulary;
    private String definition;
    private String audioUrl;
    private String pronunciation;
}
