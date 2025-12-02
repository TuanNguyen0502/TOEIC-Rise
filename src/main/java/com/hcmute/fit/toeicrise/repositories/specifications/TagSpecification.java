package com.hcmute.fit.toeicrise.repositories.specifications;

import com.hcmute.fit.toeicrise.models.entities.Tag;
import org.springframework.data.jpa.domain.Specification;

public class TagSpecification {
    public static Specification<Tag> nameContains(String name) {
        return ((root, _, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%"+name.toLowerCase()+"%"));
    }
}
