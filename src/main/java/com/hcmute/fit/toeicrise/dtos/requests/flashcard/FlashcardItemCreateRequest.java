package com.hcmute.fit.toeicrise.dtos.requests.flashcard;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FlashcardItemCreateRequest {
    @NotNull(message = MessageConstant.FLASHCARD_ITEM_VOCABULARY_NOT_NULL)
    @NotBlank(message = MessageConstant.FLASHCARD_ITEM_VOCABULARY_NOT_BLANK)
    private String vocabulary;

    @NotNull(message = MessageConstant.FLASHCARD_ITEM_DEFINITION_NOT_NULL)
    @NotBlank(message = MessageConstant.FLASHCARD_ITEM_DEFINITION_NOT_BLANK)
    private String definition;

    private String audioUrl;
    private String pronunciation;
}
