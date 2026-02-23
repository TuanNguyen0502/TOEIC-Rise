package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {
    List<Question> findAllByQuestionGroup_Id(Long questionGroupId);

    @Query("SELECT q FROM Question q " +
            "LEFT JOIN FETCH q.tags t " +
            "WHERE q.questionGroup.id IN :ids " +
            "ORDER BY q.questionGroup.position, q.position")
    List<Question> findAllByIdWithTags(@Param("ids") Set<Long> ids);

    @Query("SELECT DISTINCT q FROM Question q " +
            "LEFT JOIN FETCH q.questionGroup qg " +
            "LEFT JOIN FETCH q.tags tags " +
            "WHERE q.id IN :questionIds")
    List<Question> findAllByIdWithGroups(@Param("questionIds") List<Long> questionIds);

    @Query("SELECT DISTINCT q FROM Question q " +
            "INNER JOIN FETCH q.questionGroup qg " +
            "INNER JOIN FETCH qg.part p " +
            "INNER JOIN FETCH qg.test t " +
            "INNER JOIN FETCH q.tags tags " +
            "WHERE p.id = :partId " +
            "AND tags.id IN :tagIds " +
            "AND t.status = :status " +
            "ORDER BY qg.position, q.position")
    List<Question> findAllByPartIdAndTagIdsWithAssociations(
            @Param("partId") Long partId,
            @Param("tagIds") Set<Long> tagIds,
            @Param("status") ETestStatus status);
}
