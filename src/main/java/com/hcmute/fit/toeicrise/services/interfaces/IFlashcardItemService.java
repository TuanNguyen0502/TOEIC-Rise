package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.flashcard.FlashcardItemRequest;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardItemDetailResponse;
import org.springframework.transaction.annotation.Transactional;

public interface IFlashcardItemService {
    @Transactional
    FlashcardItemDetailResponse createFlashcardItem(FlashcardItemRequest flashcardItemRequest, String email);
}
