package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.requests.flashcard.FlashcardCreateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardItemDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardResponse;
import com.hcmute.fit.toeicrise.models.entities.Flashcard;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FlashcardMapper {
    void updateFlashcard(FlashcardCreateRequest flashcardCreateRequest, @MappingTarget Flashcard flashcard);

    default FlashcardResponse toFlashcardResponse(Flashcard flashcard) {
        return FlashcardResponse.builder()
                .id(flashcard.getId())
                .name(flashcard.getName())
                .accessType(flashcard.getAccessType())
                .itemCount(flashcard.getFlashcardItems().size())
                .favouriteCount(flashcard.getFavouriteCount())
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
