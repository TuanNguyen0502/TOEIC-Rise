package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.flashcard.FlashcardCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.flashcard.FlashcardUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardItemDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IFlashcardService {
    PageResponse getAllMyFlashcards(String email, String name, int page, int size, String sortBy, String direction);

    PageResponse getAllPublicFlashcards(String email, String name, int page, int size, String sortBy, String direction);

    FlashcardDetailResponse getFlashcardDetailById(String email, Long flashcardId);

    void createFlashcard(String email, FlashcardCreateRequest flashcardCreateRequest);

    @Transactional
    void deleteFlashcard(String email, Long flashcardId);

    @Transactional
    FlashcardResponse updateFlashcard(String email, Long flashcardId, FlashcardUpdateRequest flashcardUpdateRequest);

    Long totalFlashcards();

    List<FlashcardItemDetailResponse> getFlashcardItemDetailToReview(String email, Long flashcardId);

    List<FlashcardItemDetailResponse> getFlashcardItemDueToReview(String email);
}
