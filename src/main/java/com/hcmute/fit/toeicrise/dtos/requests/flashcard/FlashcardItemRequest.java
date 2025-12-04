package com.hcmute.fit.toeicrise.dtos.requests.flashcard;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlashcardItemRequest {
    @NotNull(message = MessageConstant.FLASHCARD_ID_NOT_NULL)
    private Long flashcardId;

    @NotBlank(message = MessageConstant.FLASHCARD_ITEM_VOCABULARY_NOT_BLANK)
    @Pattern(regexp = Constant.FLASHCARD_ITEM_NAME_PATTERN, message = MessageConstant.FLASHCARD_ITEM_VOCABULARY_INVALID)
    private String vocabulary;

    @NotBlank(message = MessageConstant.FLASHCARD_ITEM_DEFINITION_NOT_BLANK)
    @Max(value = 3000, message = MessageConstant.FLASHCARD_ITEM_DEFINITION_SIZE)
    private String definition;

    private String audioUrl;
    private String pronunciation;
}
