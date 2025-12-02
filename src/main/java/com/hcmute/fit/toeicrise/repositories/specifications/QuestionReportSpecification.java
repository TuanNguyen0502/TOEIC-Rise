package com.hcmute.fit.toeicrise.repositories.specifications;

import com.hcmute.fit.toeicrise.models.entities.QuestionReport;
import com.hcmute.fit.toeicrise.models.enums.EQuestionReportStatus;
import org.springframework.data.jpa.domain.Specification;

public class QuestionReportSpecification {
    public static Specification<QuestionReport> hasStatus(EQuestionReportStatus status) {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<QuestionReport> hasResolverId(Long resolverId) {
        return (root, _, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("resolver").get("id"), resolverId);
    }
}
