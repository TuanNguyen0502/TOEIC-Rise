package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.Flashcard;
import com.hcmute.fit.toeicrise.models.enums.EFlashcardAccessType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, Long>, JpaSpecificationExecutor<Flashcard> {
    @Query("SELECT f, " +
            "CASE WHEN fav.id IS NOT NULL THEN true ELSE false END as isFavourite " +
            "FROM Flashcard f " +
            "LEFT JOIN FlashcardFavourite fav ON f.id = fav.flashcard.id AND fav.user.id = :userId " +
            "WHERE f.accessType = :accessType " +
            "AND (:name IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Object[]> findPublicFlashcardsWithFavouriteStatus(
            @Param("userId") Long userId,
            @Param("accessType") EFlashcardAccessType accessType,
            @Param("name") String name,
            Pageable pageable
    );
}
