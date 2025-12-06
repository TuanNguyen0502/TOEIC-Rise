package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.requests.flashcard.FlashcardItemUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardItemDetailResponse;
import com.hcmute.fit.toeicrise.models.entities.FlashcardItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FlashcardItemMapper {
    FlashcardItem toFlashcardItem(FlashcardItemUpdateRequest flashcardItemUpdateRequest);

    default FlashcardItemDetailResponse toFlashcardItemDetailResponse(FlashcardItem flashcardItem) {
        return FlashcardItemDetailResponse.builder()
                .id(flashcardItem.getId())
                .vocabulary(flashcardItem.getVocabulary())
                .definition(flashcardItem.getDefinition())
                .audioUrl(flashcardItem.getAudioUrl())
                .pronunciation(flashcardItem.getPronunciation())
                .build();
    }
}
