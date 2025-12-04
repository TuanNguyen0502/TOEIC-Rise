package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.flashcard.FlashcardItemUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardItemDetailResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Flashcard;
import com.hcmute.fit.toeicrise.models.entities.FlashcardItem;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.FlashcardItemMapper;
import com.hcmute.fit.toeicrise.repositories.FlashcardItemRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IFlashcardItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlashcardItemServiceImpl implements IFlashcardItemService {
    private final FlashcardItemRepository flashcardItemRepository;
    private final FlashcardItemMapper flashcardItemMapper;
    @Override
    public FlashcardItemDetailResponse getFlashcardItemDetail(Long id) {
        FlashcardItem flashcardItem = flashcardItemRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Flashcard item"));
        return flashcardItemMapper.toFlashcardItemDetailResponse(flashcardItem);
    }

    @Override
    public void saveAll(List<FlashcardItem> flashcardItems) {
        flashcardItemRepository.saveAll(flashcardItems);
    }

    @Transactional
    @Override
    public FlashcardItem updateFlashcardItem(FlashcardItemUpdateRequest flashcardItemUpdateRequest) {
        FlashcardItem flashcardItem = flashcardItemMapper.toFlashcardItem(flashcardItemUpdateRequest);
        return flashcardItemRepository.save(flashcardItem);
    }

    @Transactional
    @Override
    public void deleteFlashcardItem(Long id) {
        flashcardItemRepository.findById(id).ifPresent(flashcardItemRepository::delete);
    }

    @Override
    public FlashcardItem createFlashcardItem(FlashcardItemUpdateRequest flashcardItemUpdateRequest, Flashcard flashcard) {
        FlashcardItem flashcardItem = flashcardItemMapper.toFlashcardItem(flashcardItemUpdateRequest);
        flashcardItem.setFlashcard(flashcard);
        return flashcardItemRepository.save(flashcardItem);
    }
}
