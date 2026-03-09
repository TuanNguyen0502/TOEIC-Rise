package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.flashcard.FlashcardItemListRequest;
import com.hcmute.fit.toeicrise.dtos.requests.flashcard.FlashcardItemProgressRequest;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Flashcard;
import com.hcmute.fit.toeicrise.models.entities.FlashcardItem;
import com.hcmute.fit.toeicrise.models.entities.FlashcardItemProgress;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.EFlashcardAccessType;
import com.hcmute.fit.toeicrise.models.enums.ELevel;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.repositories.FlashcardItemProgressRepository;
import com.hcmute.fit.toeicrise.repositories.FlashcardItemRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IFlashcardItemProgressService;
import com.hcmute.fit.toeicrise.services.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlashcardItemProgressServiceImpl implements IFlashcardItemProgressService {
    private final FlashcardItemProgressRepository flashcardItemProgressRepository;
    private final FlashcardItemRepository flashcardItemRepository;
    private final IUserService userService;

    @Override
    public List<FlashcardItemProgress> getFlashcardItemProgress(String email) {
        return flashcardItemProgressRepository.getAllFlashcardItemProgressByNextReviewAt(email, LocalDate.now());
    }

    @Override
    public void saveFlashcardItemProgress(String email, FlashcardItemListRequest flashcardItemProgress) {
        if (flashcardItemProgress == null || flashcardItemProgress.getItems().isEmpty())
            throw new AppException(ErrorCode.VALIDATION_ERROR, "No flashcard item progress found");
        User user = userService.getUserByEmail(email);

        List<Long> ids = flashcardItemProgress.getItems().stream().map(FlashcardItemProgressRequest::getFlashcardItemId).toList();
        Map<Long, FlashcardItemProgress> existingProgressByItemId = flashcardItemProgressRepository.getAllFlashcardItemProgressByUserIdAndIds(user.getId(), ids)
                .stream()
                .collect(Collectors.toMap(fp -> fp.getFlashcardItem().getId(), fp -> fp));
        List<FlashcardItemProgress> toSave = flashcardItemProgress.getItems().stream().map(request -> {
            FlashcardItemProgress progress = existingProgressByItemId.get(request.getFlashcardItemId());
            if (progress == null)
                return createNewProgress(request, user, email);
            else return updateExistingProgress(progress, request.isCorrect(), email);
        }).toList();

        flashcardItemProgressRepository.saveAll(toSave);
    }

    private FlashcardItemProgress createNewProgress(FlashcardItemProgressRequest request, User user, String email) {
        FlashcardItem item = flashcardItemRepository.findById(request.getFlashcardItemId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Flashcard Item"));

        validateOwnership(item.getFlashcard(), email);
        ELevel level = request.isCorrect() ? ELevel.LEVEL_1 : ELevel.LEVEL_0;

        return FlashcardItemProgress.builder()
                .user(user)
                .flashcardItem(item)
                .level(level)
                .nextReviewAt(LocalDateTime.now().plus(level.getDuration()))
                .build();
    }

    private FlashcardItemProgress updateExistingProgress(FlashcardItemProgress progress, boolean isCorrect, String email) {
        validateOwnership(progress.getFlashcardItem().getFlashcard(), email);
        ELevel currentLevel = progress.getLevel() != null ? progress.getLevel() : ELevel.LEVEL_0;
        ELevel newLevel = currentLevel.next(isCorrect);
        progress.setLevel(newLevel);
        progress.setNextReviewAt(LocalDateTime.now().plus(newLevel.getDuration()));
        return progress;
    }

    private void validateOwnership(Flashcard flashcard, String email) {
        boolean isPrivate = flashcard.getAccessType() == EFlashcardAccessType.PRIVATE;
        boolean isOwner = flashcard.getUser().getAccount().getEmail().equals(email);

        if (isPrivate && !isOwner)
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Flashcard Item");
    }
}
