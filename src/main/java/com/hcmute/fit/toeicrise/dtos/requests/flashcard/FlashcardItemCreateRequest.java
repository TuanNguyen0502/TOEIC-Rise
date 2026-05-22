package com.hcmute.fit.toeicrise.dtos.requests.flashcard;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlashcardItemCreateRequest {
    @NotBlank(message = MessageConstant.FLASHCARD_ITEM_VOCABULARY_NOT_BLANK)
    private String vocabulary;

    @NotBlank(message = MessageConstant.FLASHCARD_ITEM_DEFINITION_NOT_BLANK)
    private String definition;

    private String audioUrl;
    private String pronunciation;
}
