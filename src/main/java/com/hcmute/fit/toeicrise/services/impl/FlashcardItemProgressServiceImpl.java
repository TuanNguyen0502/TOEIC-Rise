package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.models.entities.FlashcardItemProgress;
import com.hcmute.fit.toeicrise.repositories.FlashcardItemProgressRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IFlashcardItemProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlashcardItemProgressServiceImpl implements IFlashcardItemProgressService {
    private final FlashcardItemProgressRepository flashcardItemProgressRepository;

    @Override
    public List<FlashcardItemProgress> getFlashcardItemProgress(String email) {
        return flashcardItemProgressRepository.getAllFlashcardItemProgressByNextReviewAt(email, LocalDate.now());
    }
}
