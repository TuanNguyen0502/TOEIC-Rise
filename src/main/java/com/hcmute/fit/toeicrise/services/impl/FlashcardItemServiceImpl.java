package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.flashcard.FlashcardItemRequest;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardItemDetailResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Flashcard;
import com.hcmute.fit.toeicrise.models.entities.FlashcardItem;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.FlashcardItemMapper;
import com.hcmute.fit.toeicrise.repositories.FlashcardItemRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IAuthenticationService;
import com.hcmute.fit.toeicrise.services.interfaces.IFlashcardItemService;
import com.hcmute.fit.toeicrise.services.interfaces.IFlashcardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FlashcardItemServiceImpl implements IFlashcardItemService {
    private final FlashcardItemRepository flashcardItemRepository;
    private final FlashcardItemMapper flashcardItemMapper;
    private final IAuthenticationService authenticationService;
    private final IFlashcardService flashcardService;

    @Transactional
    @Override
    public FlashcardItemDetailResponse createFlashcardItem(FlashcardItemRequest flashcardItemRequest, String email) {
        authenticationService.getCurrentUser(email);
        Flashcard flashcard = flashcardService.findFlashcardById(flashcardItemRequest.getFlashcardId()).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Flashcard"));
        if (!flashcard.getUser().getAccount().getEmail().equals(email)) throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Flashcard");
        FlashcardItem flashcardItem = flashcardItemMapper.toFlashcardItem(flashcardItemRequest);
        flashcardItem.setFlashcard(flashcard);
        flashcardItem = flashcardItemRepository.save(flashcardItem);
        return flashcardItemMapper.toFlashcardItemDetailResponse(flashcardItem);
    }
}
