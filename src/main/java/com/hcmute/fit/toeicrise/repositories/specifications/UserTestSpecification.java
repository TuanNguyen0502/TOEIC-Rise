package com.hcmute.fit.toeicrise.repositories.specifications;

import com.hcmute.fit.toeicrise.models.entities.UserTest;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class UserTestSpecification {
    public static Specification<UserTest> createdAtBetween(Integer days){
        return (root, _, criteriaBuilder) -> {
            if (days != null){
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime start = now.minusDays(days);
                return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), start);
            }
            return null;
        };
    }

    public static Specification<UserTest> accountHasEmail(String email){
        return (root, query, criteriaBuilder) -> {
            if (email != null){
                return criteriaBuilder.like(root.get("user").get("account").get("email"), "%" + email + "%");
            }
            return null;
        };
    }
    public static Specification<UserTest> statusEquals(ETestStatus status) {
        return (root, _, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("test").get("status"), status);
    }
}
