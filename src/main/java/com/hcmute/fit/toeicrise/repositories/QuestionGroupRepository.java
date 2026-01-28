package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionGroupRepository extends JpaRepository<QuestionGroup, Long> {
    List<QuestionGroup> findByTest_IdOrderByPositionAsc(Long id);

    @Query("SELECT DISTINCT qg " +
            "FROM QuestionGroup qg " +
            "LEFT JOIN FETCH  qg.questions q " +
            "LEFT JOIN FETCH qg.part p " +
            "WHERE qg.test.id = :testId AND p.id IN :partIds " +
            "ORDER BY p.id, qg.position, q.position")
    List<QuestionGroup> findByTest_IdAndPart_IdOrderByPositionAsc(Long testId, List<Long> partIds);
}