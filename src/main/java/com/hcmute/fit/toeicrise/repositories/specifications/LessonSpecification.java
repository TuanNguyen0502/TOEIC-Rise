package com.hcmute.fit.toeicrise.repositories.specifications;

import com.hcmute.fit.toeicrise.models.entities.Lesson;
import com.hcmute.fit.toeicrise.models.enums.ELessonLevel;
import org.springframework.data.jpa.domain.Specification;

public class LessonSpecification {
    public static Specification<Lesson> learningPathIdEquals(Long learningPathId) {
        return ((root, _, criteriaBuilder) ->
                learningPathId == null ? null : criteriaBuilder.equal(root.get("learningPath").get("id"), learningPathId));
    }

    public static Specification<Lesson> nameContains(String name) {
        return ((root, _, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(root.get("title"), "%" + name + "%"));
    }

    public static Specification<Lesson> lessonLevelEquals(ELessonLevel level){
        return ((root, _, criteriaBuilder) ->
                level == null ? null : criteriaBuilder.equal(root.get("level"), level));
    }
}
