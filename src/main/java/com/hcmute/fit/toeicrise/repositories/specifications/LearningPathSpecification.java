package com.hcmute.fit.toeicrise.repositories.specifications;

import com.hcmute.fit.toeicrise.models.entities.LearningPath;
import org.springframework.data.jpa.domain.Specification;

public class LearningPathSpecification {
    public static Specification<LearningPath> containsName(String name){
        return (root, _, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%"+ name.toLowerCase() +"%");
    }

    public static Specification<LearningPath> hasIsActive(Boolean isActive) {
        return (root, _, cb) -> isActive == null ? null : cb.equal(root.get("isActive"), isActive);
    }
}
