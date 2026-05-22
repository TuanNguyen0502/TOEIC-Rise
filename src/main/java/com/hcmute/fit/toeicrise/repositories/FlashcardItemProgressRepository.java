package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.FlashcardItemProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlashcardItemProgressRepository extends JpaRepository<FlashcardItemProgress, Long> {
    @Query("SELECT fp " +
            "FROM FlashcardItemProgress fp " +
            "LEFT JOIN FETCH fp.flashcardItem fi " +
            "WHERE fp.user.account.email = :email AND fp.nextReviewAt <= :nextReviewAt " +
            "ORDER BY fp.nextReviewAt ASC ")
    List<FlashcardItemProgress> getAllFlashcardItemProgressByNextReviewAt(@Param("email") String email, @Param("nextReviewAt") LocalDateTime nextReviewAt);

    @Query("SELECT fp " +
            "FROM FlashcardItemProgress fp "+
            "LEFT JOIN FETCH fp.flashcardItem fi " +
            "WHERE fp.user.id = :userId AND fi.id IN :ids")
    List<FlashcardItemProgress> getAllFlashcardItemProgressByUserIdAndIds(@Param("userId") Long userId, @Param("ids") List<Long> ids);

    Long countByUserIdAndUpdatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
    Long countByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
    @Query("SELECT COUNT(fp) " +
            "FROM FlashcardItemProgress fp " +
            "WHERE fp.user.id = :userId AND fp.nextReviewAt <= :nextReviewAt")
    Long countByUserIdAndNextReviewAtBefore(@Param("userId") Long userId, LocalDateTime nextReviewAt);
}
