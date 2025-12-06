package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.Question;
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
            "WHERE p.id=:partId AND tg.id IN :ids")
    List<Question> findAllByPartIdAndTags(@Param("ids") Set<Long> ids, @Param("partId") Long partId);
}
