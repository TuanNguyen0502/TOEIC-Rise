package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface QuestionGroupRepository extends JpaRepository<QuestionGroup, Long> {
    Optional<QuestionGroup> findById(Long id);
    List<QuestionGroup> findByTest_IdOrderByPositionAsc(Long id);
    @Query("SELECT qg FROM QuestionGroup qg INNER JOIN Test t ON " +
            "t.id=qg.test.id WHERE qg.test.id=:testId AND qg.part.id=:partId " +
            "ORDER BY qg.position ASC")
    List<QuestionGroup> findByTest_IdAndPart_Id(Long testId, Long partId);
}