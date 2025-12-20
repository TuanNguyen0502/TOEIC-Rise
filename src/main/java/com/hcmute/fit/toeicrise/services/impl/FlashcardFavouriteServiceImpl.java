package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardPublicResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Flashcard;
import com.hcmute.fit.toeicrise.models.entities.FlashcardFavourite;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.EFlashcardAccessType;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.FlashcardMapper;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.repositories.FlashcardFavouriteRepository;
import com.hcmute.fit.toeicrise.repositories.FlashcardRepository;
import com.hcmute.fit.toeicrise.repositories.UserRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.FlashcardFavouriteSpecification;
import com.hcmute.fit.toeicrise.services.interfaces.IFlashcardFavouriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FlashcardFavouriteServiceImpl implements IFlashcardFavouriteService {
    private final UserRepository userRepository;
    private final FlashcardRepository flashcardRepository;
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

        Page<FlashcardPublicResponse> flashcardPage = flashcardFavouriteRepository.findAll(specification, pageable)
                .map(flashcardMapper::toFlashcardPublicResponse);
        return pageResponseMapper.toPageResponse(flashcardPage);
    }

    @Override
    public void addFavourite(String email, Long flashcardId) {
        User user = userRepository.findByAccount_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User"));
        Flashcard flashcard = flashcardRepository.findById(flashcardId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Flashcard"));

        // Only allow adding public flashcards or own flashcards
        if (flashcard.getAccessType() != EFlashcardAccessType.PUBLIC &&
                !flashcard.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Flashcard");
        }

        // Check if already favourite
        boolean exists = flashcardFavouriteRepository.existsByFlashcardAndUser(flashcard, user);
        if (exists) {
            throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Flashcard favourite");
        }

        FlashcardFavourite flashcardFavourite = FlashcardFavourite.builder()
                .user(user)
                .flashcard(flashcard)
                .build();
        flashcardFavouriteRepository.save(flashcardFavourite);
    }

    @Transactional
    @Override
    public void deleteFavourite(String email, Long favouriteFlashcardId) {
        userRepository.findByAccount_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User"));
        FlashcardFavourite flashcardFavourite = flashcardFavouriteRepository.findByFlashcard_Id(favouriteFlashcardId).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Favourite flashcard"));
        flashcardFavouriteRepository.delete(flashcardFavourite);
    }
}
