package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardItemDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardPublicResponse;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardResponse;
import com.hcmute.fit.toeicrise.models.entities.Flashcard;
import com.hcmute.fit.toeicrise.models.entities.FlashcardFavourite;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FlashcardMapper {
    default FlashcardResponse toFlashcardResponse(Flashcard flashcard) {
        return FlashcardResponse.builder()
                .id(flashcard.getId())
                .authorFullName(flashcard.getUser().getFullName())
                .name(flashcard.getName())
                .accessType(flashcard.getAccessType())
                .itemCount(flashcard.getFlashcardItems().size())
                .favouriteCount(flashcard.getFavouriteCount())
                .build();
    }

    default FlashcardResponse toFlashcardResponse(FlashcardFavourite flashcardFavourite) {
        Flashcard flashcard = flashcardFavourite.getFlashcard();
        return FlashcardResponse.builder()
                .id(flashcard.getId())
                .authorFullName(flashcard.getUser().getFullName())
                .name(flashcard.getName())
                .accessType(flashcard.getAccessType())
                .itemCount(flashcard.getFlashcardItems().size())
                .favouriteCount(flashcard.getFavouriteCount())
                .build();
    }

    default FlashcardPublicResponse toFlashcardPublicResponse(Flashcard flashcard, boolean isFavourite) {
        return FlashcardPublicResponse.builder()
                .id(flashcard.getId())
                .authorFullName(flashcard.getUser().getFullName())
                .name(flashcard.getName())
                .accessType(flashcard.getAccessType())
                .itemCount(flashcard.getFlashcardItems().size())
                .favouriteCount(flashcard.getFavouriteCount())
                .isFavourite(isFavourite)
                .build();
    }

    default FlashcardDetailResponse toFlashcardDetailResponse(Flashcard flashcard, List<FlashcardItemDetailResponse> items) {
        return FlashcardDetailResponse.builder()
                .id(flashcard.getId())
                .authorFullName(flashcard.getUser().getFullName())
                .name(flashcard.getName())
                .description(flashcard.getDescription())
                .accessType(flashcard.getAccessType())
                .favouriteCount(flashcard.getFavouriteCount())
                .itemCount(items.size())
                .updatedAt(flashcard.getUpdatedAt().toString())
                .items(items)
                .build();
    }
}
