package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardItemDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Flashcard;
import com.hcmute.fit.toeicrise.models.enums.EFlashcardAccessType;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.FlashcardItemMapper;
import com.hcmute.fit.toeicrise.models.mappers.FlashcardMapper;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.repositories.FlashcardRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.FlashcardSpecification;
import com.hcmute.fit.toeicrise.services.interfaces.IFlashcardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlashcardServiceImpl implements IFlashcardService {
    private final FlashcardRepository flashcardRepository;
    private final FlashcardMapper flashcardMapper;
    private final FlashcardItemMapper flashcardItemMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    public List<FlashcardResponse> getAllFlashcardsByEmail(String email) {
        return flashcardRepository.findAllByUser_Account_Email(email)
                .stream()
                .map(flashcardMapper::toFlashcardResponse)
                .toList();
    }

    @Override
    public PageResponse getAllPublicFlashcards(String name, int page, int size, String sortBy, String direction) {
        Specification<Flashcard> specification = (_, _, cb) -> cb.conjunction();
        specification = specification.and(FlashcardSpecification.accessTypeEquals(EFlashcardAccessType.PUBLIC));
        if (name != null && !name.isBlank()) {
            specification = specification.and(FlashcardSpecification.nameContains(name));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<FlashcardResponse> flashcardPage = flashcardRepository.findAll(specification, pageable)
                .map(flashcardMapper::toFlashcardResponse);
        return pageResponseMapper.toPageResponse(flashcardPage);
    }

    @Override
    public FlashcardDetailResponse getFlashcardDetailById(String email, Long flashcardId) {
        Flashcard flashcard = flashcardRepository.findById(flashcardId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Flashcard"));

        // Check access
        if (flashcard.getAccessType() == EFlashcardAccessType.PRIVATE &&
                !flashcard.getUser().getAccount().getEmail().equals(email)) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Flashcard");
        }

        List<FlashcardItemDetailResponse> items = flashcard.getFlashcardItems()
                .stream()
                .map(flashcardItemMapper::toFlashcardItemDetailResponse)
                .toList();

        return flashcardMapper.toFlashcardDetailResponse(flashcard, items);
    }
}
