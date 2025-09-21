package com.hcmute.fit.toeicrise.repositories.specifications;

import com.hcmute.fit.toeicrise.models.entities.Question;
import org.springframework.data.jpa.domain.Specification;

public class QuestionSpecification {
    public static Specification<Question> hasPart(String part) {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("questionGroup").get("part").get("name"), part);
    }
}
