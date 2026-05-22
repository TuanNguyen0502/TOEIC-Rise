package com.hcmute.fit.toeicrise.repositories.specifications;

import com.hcmute.fit.toeicrise.models.entities.SystemPrompt;
import com.hcmute.fit.toeicrise.models.enums.ESystemPromptFeatureType;
import org.springframework.data.jpa.domain.Specification;

public class SystemPromptSpecification {
    public static Specification<SystemPrompt> hasFeatureType(ESystemPromptFeatureType featureType) {
        return (root, _, criteriaBuilder) ->
                featureType == null ? null : criteriaBuilder.equal(root.get("featureType"), featureType);
    }

    public static Specification<SystemPrompt> isActive(Boolean isActive) {
        return (root, _, criteriaBuilder) ->
                isActive == null ? null : criteriaBuilder.equal(root.get("isActive"), isActive);
    }

    public static Specification<SystemPrompt> versionGreaterThan(Integer version) {
        return (root, _, criteriaBuilder) ->
                version == null ? null : criteriaBuilder.greaterThan(root.get("version"), version);
    }
}
