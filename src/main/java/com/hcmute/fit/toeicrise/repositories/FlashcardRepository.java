package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, Long>, JpaSpecificationExecutor<Flashcard> {
    List<Flashcard> findAllByUser_Account_Email(String userAccountEmail);
}
