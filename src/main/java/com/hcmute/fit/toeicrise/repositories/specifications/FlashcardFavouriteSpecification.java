package com.hcmute.fit.toeicrise.repositories.specifications;

import com.hcmute.fit.toeicrise.models.entities.FlashcardFavourite;
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
}
