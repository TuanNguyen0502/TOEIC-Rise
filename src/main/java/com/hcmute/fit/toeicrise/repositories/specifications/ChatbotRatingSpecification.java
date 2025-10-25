package com.hcmute.fit.toeicrise.repositories.specifications;

import com.hcmute.fit.toeicrise.models.entities.ChatbotRating;
import com.hcmute.fit.toeicrise.models.enums.EChatbotRating;
import org.springframework.data.jpa.domain.Specification;

public class ChatbotRatingSpecification {
    public static Specification<ChatbotRating> hasRating(EChatbotRating rating) {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("rating"), rating);
    }

    public static Specification<ChatbotRating> hasConversationTitle(String conversationTitle) {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.like(root.get("conversationTitle"), "%" + conversationTitle + "%");
    }
}
