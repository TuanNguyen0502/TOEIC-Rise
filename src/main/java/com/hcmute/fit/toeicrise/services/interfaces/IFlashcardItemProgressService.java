package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.models.entities.FlashcardItemProgress;

import java.util.List;

public interface IFlashcardItemProgressService {
    List<FlashcardItemProgress> getFlashcardItemProgress(String email);
}
