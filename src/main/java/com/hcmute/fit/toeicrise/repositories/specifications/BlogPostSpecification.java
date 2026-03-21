package com.hcmute.fit.toeicrise.repositories.specifications;

import com.hcmute.fit.toeicrise.models.entities.BlogPost;
import com.hcmute.fit.toeicrise.models.enums.EBlogPostStatus;
import org.springframework.data.jpa.domain.Specification;

public class BlogPostSpecification {
    public static Specification<BlogPost> byCategorySlug(String category) {
        return (root, _, criteriaBuilder) ->
                category == null ? null : criteriaBuilder.equal(root.get("category").get("slug"), category);
    }

    public static Specification<BlogPost> titleContains(String title) {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    public static Specification<BlogPost> slugContains(String slug) {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.like(root.get("slug"), "%" + slug + "%");
    }

    public static Specification<BlogPost> isActive(EBlogPostStatus status) {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }
}
