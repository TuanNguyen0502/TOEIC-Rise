package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.models.entities.Flashcard;
import com.hcmute.fit.toeicrise.models.entities.User;
import org.springframework.transaction.annotation.Transactional;

public interface IFlashcardFavouriteService {
    PageResponse getAllMyFavouriteFlashcards(String email, String name, int page, int size, String sortBy, String direction);

    void addFavourite(String email, Long flashcardId);

    @Transactional
    void deleteFavourite(String email, Long flashcardId);

    boolean isFlashcardFavouriteByUser(User user, Flashcard flashcard);

    void deleteByFlashcard(Flashcard flashcard);
}
