package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import org.springframework.data.jpa.repository.EntityGraph;
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

    @Query("SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.tags WHERE q.id IN :ids")
    List<Question> findAllByIdWithTags(@Param("ids") Set<Long> ids);

    @Query("SELECT DISTINCT q " +
            "FROM Question q " +
            "LEFT JOIN q.tags tg " +
            "LEFT JOIN q.questionGroup.part p " +
            "WHERE p.id=:partId AND tg.id=:id AND q.questionGroup.test.status=:status")
    List<Question> findAllByPartIdAndTag(@Param("id") Long ids, @Param("partId") Long partId, @Param("status")ETestStatus status);

    @EntityGraph(attributePaths = {
            "questionGroup",
            "questionGroup.test",
            "questionGroup.part",
            "tags"
    })
    @Query("SELECT DISTINCT q FROM Question q " +
            "WHERE q.questionGroup.part.id = :partId " +
            "AND q.questionGroup.test.status = :status " +
            "AND EXISTS (SELECT t FROM q.tags t WHERE t.id IN :tagIds)") // Subquery check tag tồn tại
    List<Question> findQuestionsForMiniTest(
            @Param("partId") Long partId,
            @Param("tagIds") Set<Long> tagIds,
            @Param("status") ETestStatus status
    );
}
