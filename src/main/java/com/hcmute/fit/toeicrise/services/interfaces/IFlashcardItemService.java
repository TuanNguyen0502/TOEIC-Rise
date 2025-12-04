package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.flashcard.FlashcardItemUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardItemDetailResponse;
import com.hcmute.fit.toeicrise.models.entities.Flashcard;
import com.hcmute.fit.toeicrise.models.entities.FlashcardItem;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IFlashcardItemService {
    FlashcardItemDetailResponse getFlashcardItemDetail(Long id);
    @Transactional
    void saveAll(List<FlashcardItem> flashcardItems);
    @Transactional
    FlashcardItem updateFlashcardItem(FlashcardItemUpdateRequest flashcardItemUpdateRequest);
    @Transactional
    void deleteFlashcardItem(Long id);
    @Transactional
    FlashcardItem createFlashcardItem(FlashcardItemUpdateRequest flashcardItemUpdateRequest, Flashcard flashcard);
}
