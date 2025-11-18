package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestHistoryResponse;
import com.hcmute.fit.toeicrise.models.entities.Test;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<Test, Long>, JpaSpecificationExecutor<Test> {
    Optional<Test> findByName(String name);

    List<Test> findAllByTestSet_Id(Long testSetId);

    @Query("SELECT new com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestHistoryResponse(" +
            "ut.id, ut.createdAt, ut.parts, ut.correctAnswers,ut.totalQuestions, ut.totalScore, ut.timeSpent) " +
            "FROM Test t " +
            "INNER JOIN UserTest ut ON t.id = ut.test.id " +
            "WHERE t.id = :id AND ut.user.account.email = :email " +
            "ORDER BY ut.createdAt DESC ")
    List<LearnerTestHistoryResponse> getLearnerTestHistoryByTest_IdAndUser_Email(@Param("id") Long testId, @Param("email") String email);

    @Query(value = "SELECT t.id, t.name, t.number_of_learner_tests, p.name, p.id," +
            "group_concat(distinct tg.name order by tg.name separator '; ') as tags " +
            "FROM tests t " +
            "INNER JOIN question_groups qg ON qg.test_id = t.id " +
            "INNER JOIN questions q ON q.question_group_id = qg.id " +
            "INNER JOIN parts p ON qg.part_id = p.id " +
            "LEFT JOIN questions_tags qtg ON qtg.question_id = q.id " +
            "LEFT JOIN tags tg ON qtg.tag_id = tg.id " +
            "WHERE t.id =:id AND t.status = 'APPROVED'" +
            "GROUP BY t.id, t.name, t.number_of_learner_tests, p.name, p.id " +
            "ORDER BY p.id", nativeQuery = true)
    List<Object[]> findListTagByIdOrderByPartName(@Param("id") Long id);

    Optional<Test> findByIdAndStatus(Long id, ETestStatus status);
}
