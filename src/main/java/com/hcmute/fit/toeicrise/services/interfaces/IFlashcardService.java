package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardResponse;

import java.util.List;

public interface IFlashcardService {
    List<FlashcardResponse> getAllFlashcardsByEmail(String email);

    PageResponse getAllPublicFlashcards(String name, int page, int size, String sortBy, String direction);
}
