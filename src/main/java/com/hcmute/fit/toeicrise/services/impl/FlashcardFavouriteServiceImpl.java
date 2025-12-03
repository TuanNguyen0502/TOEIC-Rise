package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardResponse;
import com.hcmute.fit.toeicrise.models.entities.FlashcardFavourite;
import com.hcmute.fit.toeicrise.models.enums.EFlashcardAccessType;
import com.hcmute.fit.toeicrise.models.mappers.FlashcardMapper;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.repositories.FlashcardFavouriteRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.FlashcardFavouriteSpecification;
import com.hcmute.fit.toeicrise.services.interfaces.IFlashcardFavouriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FlashcardFavouriteServiceImpl implements IFlashcardFavouriteService {
    private final FlashcardFavouriteRepository flashcardFavouriteRepository;
    private final FlashcardMapper flashcardMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    public PageResponse getAllMyFavouriteFlashcards(String email, String name, int page, int size, String sortBy, String direction) {
        Specification<FlashcardFavourite> specification = (_, _, cb) -> cb.conjunction();
        // Filter by user's email
        specification = specification.and(FlashcardFavouriteSpecification.emailEquals(email));
        // Only public flashcards
        specification = specification.and(FlashcardFavouriteSpecification.accessTypeEquals(EFlashcardAccessType.PUBLIC));
        if (name != null && !name.isBlank()) {
            specification = specification.and(FlashcardFavouriteSpecification.nameContains(name));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<FlashcardResponse> flashcardPage = flashcardFavouriteRepository.findAll(specification, pageable)
                .map(flashcardMapper::toFlashcardResponse);
        return pageResponseMapper.toPageResponse(flashcardPage);
    }
}
