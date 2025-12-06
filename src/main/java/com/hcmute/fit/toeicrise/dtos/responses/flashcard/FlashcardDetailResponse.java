package com.hcmute.fit.toeicrise.dtos.responses.flashcard;

import com.hcmute.fit.toeicrise.models.enums.EFlashcardAccessType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FlashcardDetailResponse {
    private Long id;
    private String authorFullName;
    private String name;
    private String description;
    private EFlashcardAccessType accessType;
    private Integer favouriteCount;
    private Integer itemCount;
    private String updatedAt;
    private List<FlashcardItemDetailResponse> items;
}
