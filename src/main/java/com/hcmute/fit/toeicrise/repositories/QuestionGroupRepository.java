package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface QuestionGroupRepository extends JpaRepository<QuestionGroup, Long> {
    List<QuestionGroup> findByTest_IdOrderByPositionAsc(Long id);

    List<QuestionGroup> findByTest_IdAndPart_IdOrderByPositionAsc(Long testId, Long partId);

    @Query("SELECT DISTINCT qg FROM QuestionGroup qg " +
            "LEFT JOIN FETCH qg.questions " +
            "WHERE qg.id IN :ids")
    List<QuestionGroup> findAllByIdInFetchQuestions(@Param("ids") Set<Long> ids);
}