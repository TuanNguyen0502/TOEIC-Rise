package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestHistoryResponse;
import com.hcmute.fit.toeicrise.models.entities.Test;
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
            "ut.id, ut.createdAt, ut.parts, ut.correctAnswers, ut.score, ut.timeSpent) " +
            "FROM Test t " +
            "INNER JOIN UserTest ut ON t.id = ut.test.id " +
            "WHERE t.id = :id AND ut.user.account.email = :email")
    List<LearnerTestHistoryResponse> getLearnerTestHistoryByTest_IdAndUser_Email(@Param("id") Long testId, @Param("email") String email);
}
