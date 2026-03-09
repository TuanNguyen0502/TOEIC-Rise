package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.flashcard.FlashcardItemListRequest;
import com.hcmute.fit.toeicrise.dtos.requests.flashcard.FlashcardItemProgressRequest;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardItemDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardReviewOverallResponse;

import java.util.List;

public interface IFlashcardItemProgressService {
    List<FlashcardItemDetailResponse> getFlashcardItemDueToReview(String email);
    void saveFlashcardItemProgress(String email, FlashcardItemListRequest flashcardItemProgress);
    FlashcardReviewOverallResponse getFlashcardReviewOverall(String email);
}
