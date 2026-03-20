package com.hcmute.fit.toeicrise.repositories.specifications;

import com.hcmute.fit.toeicrise.models.entities.BlogPost;
import org.springframework.data.jpa.domain.Specification;

public class BlogPostSpecification {
    public static Specification<BlogPost> byCategorySlug(String category) {
        return (root, _, criteriaBuilder) ->
                category == null ? null : criteriaBuilder.equal(root.get("category").get("slug"), category);
    }
}
