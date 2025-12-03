package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.flashcard.FlashcardCreateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IFlashcardService {
    List<FlashcardResponse> getAllFlashcardsByEmail(String email);

    PageResponse getAllPublicFlashcards(String name, int page, int size, String sortBy, String direction);

    FlashcardDetailResponse getFlashcardDetailById(String email, Long flashcardId);

    void createFlashcard(String email, FlashcardCreateRequest flashcardCreateRequest);

    @Transactional
    void deleteFlashcard(String email, Long flashcardId);
}
