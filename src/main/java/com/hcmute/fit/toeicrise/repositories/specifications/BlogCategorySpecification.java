package com.hcmute.fit.toeicrise.repositories.specifications;

import com.hcmute.fit.toeicrise.models.entities.BlogCategory;
import org.springframework.data.jpa.domain.Specification;

public class BlogCategorySpecification {
    public static Specification<BlogCategory> nameContains(String name) {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<BlogCategory> slugContains(String slug) {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("slug")), "%" + slug.toLowerCase() + "%");
    }

    public static Specification<BlogCategory> isActive(Boolean isActive) {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isActive"), isActive);
    }
}
