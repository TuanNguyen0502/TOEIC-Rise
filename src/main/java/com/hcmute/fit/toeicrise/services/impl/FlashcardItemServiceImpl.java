package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardItemDetailResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.FlashcardItem;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.FlashcardItemMapper;
import com.hcmute.fit.toeicrise.repositories.FlashcardItemRepository;
import com.hcmute.fit.toeicrise.repositories.FlashcardRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IFlashcardItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
