package com.hcmute.fit.toeicrise.repositories.specifications;

import com.hcmute.fit.toeicrise.models.entities.FlashcardFavourite;
import com.hcmute.fit.toeicrise.models.enums.EFlashcardAccessType;
import org.springframework.data.jpa.domain.Specification;

public class FlashcardFavouriteSpecification {
    public static Specification<FlashcardFavourite> emailEquals(String email) {
        return (root, _, criteriaBuilder) ->
                email == null ? null : criteriaBuilder.equal(root.get("user").get("account").get("email"), email);
    }

    public static Specification<FlashcardFavourite> nameContains(String name) {
        return (root, _, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("flashcard").get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<FlashcardFavourite> accessTypeEquals(EFlashcardAccessType accessType) {
        return (root, _, criteriaBuilder) ->
                accessType == null ? null : criteriaBuilder.equal(root.get("flashcard").get("accessType"), accessType);
    }
}
