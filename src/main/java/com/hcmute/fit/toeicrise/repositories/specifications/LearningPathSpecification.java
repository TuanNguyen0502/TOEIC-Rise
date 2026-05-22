package com.hcmute.fit.toeicrise.repositories.specifications;

import com.hcmute.fit.toeicrise.models.entities.LearningPath;
import com.hcmute.fit.toeicrise.models.enums.ELessonLevel;
import org.springframework.data.jpa.domain.Specification;

public class LearningPathSpecification {
    public static Specification<LearningPath> containsName(String name){
        return (root, _, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%"+ name.toLowerCase() +"%");
    }

    public static Specification<LearningPath> hasIsActive(Boolean isActive) {
        return (root, _, cb) -> isActive == null ? null : cb.equal(root.get("isActive"), isActive);
    }

    public static Specification<LearningPath> hasLevel(ELessonLevel level){
        return (root, _, cb) -> cb.equal(root.get("level"), level);
    }
}
