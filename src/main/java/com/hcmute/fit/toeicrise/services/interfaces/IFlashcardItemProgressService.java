package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.flashcard.FlashcardItemListRequest;
import com.hcmute.fit.toeicrise.dtos.requests.flashcard.FlashcardItemProgressRequest;
import com.hcmute.fit.toeicrise.models.entities.FlashcardItemProgress;

import java.util.List;

public interface IFlashcardItemProgressService {
    List<FlashcardItemProgress> getFlashcardItemProgress(String email);
    void saveFlashcardItemProgress(String email, FlashcardItemListRequest flashcardItemProgress);
}
