package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardResponse;
import com.hcmute.fit.toeicrise.models.entities.Flashcard;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FlashcardMapper {
    default FlashcardResponse toFlashcardResponse(Flashcard flashcard) {
        return FlashcardResponse.builder()
                .id(flashcard.getId())
                .name(flashcard.getName())
                .accessType(flashcard.getAccessType())
                .itemCount(flashcard.getFlashcardItems().size())
                .favouriteCount(flashcard.getFavouriteCount())
                .build();
    }
}
