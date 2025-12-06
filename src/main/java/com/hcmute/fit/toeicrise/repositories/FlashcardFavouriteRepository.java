package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.Flashcard;
import com.hcmute.fit.toeicrise.models.entities.FlashcardFavourite;
import com.hcmute.fit.toeicrise.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FlashcardFavouriteRepository extends JpaRepository<FlashcardFavourite, Long>, JpaSpecificationExecutor<FlashcardFavourite> {
    boolean existsByFlashcardAndUser(Flashcard flashcard, User user);
}
