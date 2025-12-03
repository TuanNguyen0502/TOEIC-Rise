package com.hcmute.fit.toeicrise.repositories.specifications;

import com.hcmute.fit.toeicrise.models.entities.Flashcard;
import com.hcmute.fit.toeicrise.models.enums.EFlashcardAccessType;
import org.springframework.data.jpa.domain.Specification;

public class FlashcardSpecification {
    public static Specification<Flashcard> accessTypeEquals(EFlashcardAccessType accessType) {
        return (root, _, criteriaBuilder) ->
                accessType == null ? null : criteriaBuilder.equal(root.get("accessType"), accessType);
    }

    public static Specification<Flashcard> nameContains(String name) {
        return (root, _, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }
}
