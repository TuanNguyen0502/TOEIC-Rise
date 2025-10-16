package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.ChatbotRating;
import com.hcmute.fit.toeicrise.models.enums.EChatbotRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatbotRatingRepository extends JpaRepository<ChatbotRating, Long>, JpaSpecificationExecutor<ChatbotRating> {
    int countByRating(EChatbotRating rating);

    Optional<ChatbotRating> findFirstByMessageId(String messageId);

    boolean existsByMessageId(String messageId);
}
