package com.hcmute.fit.toeicrise.repositories.specifications;

import com.hcmute.fit.toeicrise.models.entities.TestSet;
import com.hcmute.fit.toeicrise.models.enums.ETestSetStatus;
import org.springframework.data.jpa.domain.Specification;

public class TestSetSpecification {
    public static Specification<TestSet> nameContains(String name) {
        return (root, _, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<TestSet> statusEquals(ETestSetStatus status) {
        return (root, _, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }
}
