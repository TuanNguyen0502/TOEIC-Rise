package com.hcmute.fit.toeicrise.repositories.specifications;

import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.ERole;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> emailContains(String email) {
        return (root, _, criteriaBuilder) ->
                email == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("account").get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<User> isActiveEquals(Boolean isActive) {
        return (root, _, criteriaBuilder) ->
                isActive == null ? null : criteriaBuilder.equal(root.get("account").get("isActive"), isActive);
    }

    public static Specification<User> hasRole(ERole role) {
        return (root, _, criteriaBuilder) ->
                role == null ? null : criteriaBuilder.equal(root.get("role"), role);
    }
}
