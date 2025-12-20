package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.flashcard.FlashcardCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.flashcard.FlashcardItemUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.flashcard.FlashcardUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardItemDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardPublicResponse;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Flashcard;
import com.hcmute.fit.toeicrise.models.entities.FlashcardItem;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.EFlashcardAccessType;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.FlashcardItemMapper;
import com.hcmute.fit.toeicrise.models.mappers.FlashcardMapper;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.repositories.FlashcardRepository;
import com.hcmute.fit.toeicrise.repositories.UserRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IFlashcardFavouriteService;
import com.hcmute.fit.toeicrise.services.interfaces.IFlashcardItemService;
import com.hcmute.fit.toeicrise.services.interfaces.IFlashcardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlashcardServiceImpl implements IFlashcardService {
    private final UserRepository userRepository;
    private final FlashcardRepository flashcardRepository;
    private final IFlashcardItemService flashcardItemService;
    private final IFlashcardFavouriteService flashcardFavouriteService;
    private final FlashcardMapper flashcardMapper;
    private final FlashcardItemMapper flashcardItemMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    public PageResponse getAllMyFlashcards(String email, String name, int page, int size, String sortBy, String direction) {
        User user = userRepository.findByAccount_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Object[]> results = flashcardRepository.findMyFlashcardsWithFavouriteStatus(
                user.getId(),
                EFlashcardAccessType.PUBLIC,
                name,
                pageable
        );

        Page<FlashcardPublicResponse> flashcardPage = results.map(result -> {
            Flashcard flashcard = (Flashcard) result[0];
            boolean isFavourite = (boolean) result[1];
            return flashcardMapper.toFlashcardPublicResponse(flashcard, isFavourite);
        });

        return pageResponseMapper.toPageResponse(flashcardPage);
    }

    @Override
    public PageResponse getAllPublicFlashcards(String email, String name, int page, int size, String sortBy, String direction) {
        User user = userRepository.findByAccount_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Object[]> results = flashcardRepository.findPublicFlashcardsWithFavouriteStatus(
                user.getId(),
                EFlashcardAccessType.PUBLIC,
                name,
                pageable
        );

        Page<FlashcardPublicResponse> flashcardPage = results.map(result -> {
            Flashcard flashcard = (Flashcard) result[0];
            boolean isFavourite = (boolean) result[1];
            return flashcardMapper.toFlashcardPublicResponse(flashcard, isFavourite);
        });

        return pageResponseMapper.toPageResponse(flashcardPage);
    }

    @Override
    public FlashcardDetailResponse getFlashcardDetailById(String email, Long flashcardId) {
        User user = userRepository.findByAccount_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        Flashcard flashcard = flashcardRepository.findById(flashcardId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Flashcard"));

        boolean isOwner = flashcard.getUser().getId().equals(user.getId());
        boolean isFavourite = flashcardFavouriteService.isFlashcardFavouriteByUser(user, flashcard);

        // Check access
        if (flashcard.getAccessType() == EFlashcardAccessType.PRIVATE &&
                !flashcard.getUser().getAccount().getEmail().equals(email)) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Flashcard");
        }

        List<FlashcardItemDetailResponse> items = flashcard.getFlashcardItems()
                .stream()
                .map(flashcardItemMapper::toFlashcardItemDetailResponse)
                .toList();

        return flashcardMapper.toFlashcardDetailResponse(flashcard, isOwner, isFavourite, items);
    }

    @Override
    public void createFlashcard(String email, FlashcardCreateRequest flashcardCreateRequest) {
        User author = userRepository.findByAccount_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        Flashcard flashcard = new Flashcard();
        flashcard.setUser(author);
        flashcard.setName(flashcardCreateRequest.getName());
        flashcard.setDescription(flashcardCreateRequest.getDescription());
        flashcard.setAccessType(flashcardCreateRequest.getAccessType());
        flashcard.setFavouriteCount(0);

        // Save flashcard first to get the ID
        final Flashcard savedFlashcard = flashcardRepository.save(flashcard);

        // Create flashcard items if provided
        if (flashcardCreateRequest.getItems() != null && !flashcardCreateRequest.getItems().isEmpty()) {
            List<FlashcardItem> flashcardItems = flashcardCreateRequest.getItems().stream()
                    .map(itemRequest -> FlashcardItem.builder()
                            .flashcard(savedFlashcard)
                            .vocabulary(itemRequest.getVocabulary())
                            .definition(itemRequest.getDefinition())
                            .audioUrl(itemRequest.getAudioUrl())
                            .pronunciation(itemRequest.getPronunciation())
                            .build())
                    .toList();

            flashcardItemService.saveAll(flashcardItems);
        }
    }

    @Transactional
    @Override
    public void deleteFlashcard(String email, Long flashcardId) {
        Flashcard flashcard = flashcardRepository.findById(flashcardId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Flashcard"));

        // Check if user owns the flashcard
        if (!flashcard.getUser().getAccount().getEmail().equals(email)) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Flashcard");
        }

        flashcardRepository.delete(flashcard);
    }

    @Transactional
    @Override
    public FlashcardResponse updateFlashcard(String email, Long flashcardId, FlashcardUpdateRequest flashcardUpdateRequest) {
        Flashcard flashcard = flashcardRepository.findById(flashcardId).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Flashcard"));
        if (!flashcard.getUser().getAccount().getEmail().equals(email))
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Flashcard");

        List<FlashcardItem> currentItems = flashcard.getFlashcardItems();
        Map<Long, FlashcardItem> existingItems = currentItems.stream()
                .collect(Collectors.toMap(FlashcardItem::getId, item -> item));
        currentItems.clear();

        for (FlashcardItemUpdateRequest flashcardItem : flashcardUpdateRequest.getItems()) {
            if (flashcardItem.getId() != null && existingItems.containsKey(flashcardItem.getId())) {
                FlashcardItem item = flashcardItemService.updateFlashcardItem(flashcardItem);
                currentItems.add(item);
                existingItems.remove(flashcardItem.getId());
            } else {
                FlashcardItem newItem = flashcardItemService.createFlashcardItem(flashcardItem, flashcard);
                currentItems.add(newItem);
            }
        }
        for (FlashcardItem itemToDelete : existingItems.values()) {
            flashcardItemService.deleteFlashcardItem(itemToDelete.getId());
        }
        flashcardMapper.updateFlashcard(flashcardUpdateRequest, flashcard);
        flashcardRepository.save(flashcard);
        return flashcardMapper.toFlashcardResponse(flashcard);
    }

    @Override
    public Long totalFlashcards() {
        return flashcardRepository.count();
    }
}
