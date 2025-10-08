package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.ChatbotRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatbotRatingRepository extends JpaRepository<ChatbotRating, Long>, JpaSpecificationExecutor<ChatbotRating> {
}
