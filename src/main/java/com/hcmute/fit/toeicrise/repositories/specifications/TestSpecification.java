package com.hcmute.fit.toeicrise.repositories.specifications;

import com.hcmute.fit.toeicrise.models.entities.Test;
import org.springframework.data.jpa.domain.Specification;

public class TestSpecification {
    public static Specification<Test> testSetIdEquals(Long testSetId) {
        return (root, _, criteriaBuilder) ->
                testSetId == null ? null : criteriaBuilder.equal(root.get("testSet").get("id"), testSetId);
    }

    public static Specification<Test> nameContains(String name) {
        return (root, _, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Test> statusEquals(String status) {
        return (root, _, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }
}
