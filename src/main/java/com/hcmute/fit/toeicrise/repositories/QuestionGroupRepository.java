package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface QuestionGroupRepository extends JpaRepository<QuestionGroup, Long> {
    Optional<QuestionGroup> findById(Long id);

    List<QuestionGroup> findByTest_IdOrderByPositionAsc(Long id);

    @Query("SELECT qg FROM QuestionGroup qg LEFT JOIN FETCH qg.questions WHERE qg.id = :id")
    Optional<QuestionGroup> findWithQuestionsById(@Param("id") Long id);

    List<QuestionGroup> findByTest_IdAndPart_IdOrderByPositionAsc(Long testId, Long partId);

}