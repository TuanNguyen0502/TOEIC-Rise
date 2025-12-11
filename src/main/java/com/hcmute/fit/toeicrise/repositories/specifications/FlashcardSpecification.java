package com.hcmute.fit.toeicrise.repositories.specifications;

import com.hcmute.fit.toeicrise.models.entities.Flashcard;
import org.springframework.data.jpa.domain.Specification;

public class FlashcardSpecification {
    public static Specification<Flashcard> ownerEmailEquals(String email) {
        return (root, _, criteriaBuilder) ->
                email == null ? null : criteriaBuilder.equal(root.get("user").get("account").get("email"), email);
    }

    public static Specification<Flashcard> nameContains(String name) {
        return (root, _, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }
}
