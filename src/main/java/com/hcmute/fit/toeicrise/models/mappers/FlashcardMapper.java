package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.requests.flashcard.FlashcardUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.*;
import com.hcmute.fit.toeicrise.models.entities.Flashcard;
import com.hcmute.fit.toeicrise.models.entities.FlashcardFavourite;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FlashcardMapper {
    void updateFlashcard(FlashcardUpdateRequest flashcardUpdateRequest, @MappingTarget Flashcard flashcard);

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

    default FlashcardPublicResponse toFlashcardPublicResponse(FlashcardFavourite flashcardFavourite) {
        Flashcard flashcard = flashcardFavourite.getFlashcard();
        return FlashcardPublicResponse.builder()
                .id(flashcard.getId())
                .authorFullName(flashcard.getUser().getFullName())
                .name(flashcard.getName())
                .accessType(flashcard.getAccessType())
                .itemCount(flashcard.getFlashcardItems().size())
                .favouriteCount(flashcard.getFavouriteCount())
                .isFavourite(true)
                .build();
    }

    default FlashcardDetailResponse toFlashcardDetailResponse(
            Flashcard flashcard,
            Boolean isOwner,
            Boolean isFavourite,
            List<FlashcardItemDetailResponse> items) {
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
                .isOwner(isOwner)
                .isFavourite(isFavourite)
                .build();
    }

    default FlashcardForPopupResponse toFlashcardForPopupResponse(Flashcard flashcard) {
        return FlashcardForPopupResponse.builder()
                .id(flashcard.getId())
                .name(flashcard.getName())
                .description(flashcard.getDescription())
                .itemCount(flashcard.getFlashcardItems().size())
                .build();
    }
}
