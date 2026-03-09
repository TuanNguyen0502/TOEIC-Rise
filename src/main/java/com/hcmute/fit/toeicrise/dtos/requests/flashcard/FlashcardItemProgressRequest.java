package com.hcmute.fit.toeicrise.dtos.requests.flashcard;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlashcardItemProgressRequest {
    @NotNull(message = MessageConstant.FLASHCARD_ITEM_ID_NOT_NULL)
    private Long flashcardItemId;
    private boolean correct;
}
