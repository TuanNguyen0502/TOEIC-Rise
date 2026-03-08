package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.FlashcardItemProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlashcardItemProgressRepository extends JpaRepository<FlashcardItemProgress, Long> {
    @Query("SELECT fp " +
            "FROM FlashcardItemProgress fp " +
            "LEFT JOIN FETCH fp.flashcardItem fi " +
            "WHERE fi.flashcard.user.account.email = :email AND fp.nextReviewAt <= :nextReviewAt " +
            "ORDER BY fp.nextReviewAt ASC ")
    List<FlashcardItemProgress> getAllFlashcardItemProgressByNextReviewAt(@Param("email") String email, @Param("nextReviewAt") LocalDate nextReviewAt);
}
