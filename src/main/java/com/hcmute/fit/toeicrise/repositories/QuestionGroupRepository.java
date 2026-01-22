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
    @Query("SELECT DISTINCT qg FROM QuestionGroup qg " +
            "LEFT JOIN FETCH qg.questions q " +
            "LEFT JOIN FETCH qg.part p " +
            "LEFT JOIN FETCH q.tags t " +
            "WHERE qg.test.id = :testId " +
            "ORDER BY p.id, qg.position, q.position")
    List<QuestionGroup> findByTest_IdOrderByPositionAsc(@Param("testId") Long id);

    @Query("SELECT DISTINCT qg " +
            "FROM QuestionGroup qg " +
            "LEFT JOIN FETCH  qg.questions q " +
            "LEFT JOIN FETCH qg.part p " +
            "WHERE qg.test.id = :testId AND p.id IN :partIds " +
            "ORDER BY p.id, qg.position, q.position")
    List<QuestionGroup> findByTest_IdAndPart_IdOrderByPositionAsc(Long testId, List<Long> partIds);

    @Query("SELECT DISTINCT qg FROM QuestionGroup qg " +
            "LEFT JOIN FETCH qg.questions " +
            "WHERE qg.id IN :ids")
    List<QuestionGroup> findAllByIdInFetchQuestions(@Param("ids") Set<Long> ids);

    @Query("SELECT DISTINCT qg.id FROM QuestionGroup qg WHERE qg.id IN :ids")
    Set<Long> findExistingIdsByIds(List<Long> ids);
}