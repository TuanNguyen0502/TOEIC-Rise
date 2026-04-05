package com.hcmute.fit.toeicrise.repositories.specifications;

import com.hcmute.fit.toeicrise.models.entities.Test;
import com.hcmute.fit.toeicrise.models.enums.ETestSetStatus;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import com.hcmute.fit.toeicrise.models.enums.ETestType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class TestSpecification {
    public static Specification<Test> testSetIdEquals(Long testSetId) {
        return (root, _, criteriaBuilder) ->
                testSetId == null ? null : criteriaBuilder.equal(root.get("testSet").get("id"), testSetId);
    }

    public static Specification<Test> nameContains(String name) {
        return (root, _, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Test> statusEquals(ETestStatus status) {
        return (root, _, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Test> testSetStatusEquals(ETestSetStatus status) {
        return (root, _, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("testSet").get("status"), status);
    }

    public static Specification<Test> testSetIdsIn(List<Long> testSetIds) {
        return (root, _, _) -> {
            if (testSetIds != null && !testSetIds.isEmpty())
                return root.get("testSet").get("id").in(testSetIds);
            return null;
        };
    }

    public static Specification<Test> typeEquals(ETestType type) {
        return (root, _, criteriaBuilder) ->
                type == null ? null : criteriaBuilder.equal(root.get("type"), type);
    }
}
