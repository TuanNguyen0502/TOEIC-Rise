package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestHistoryResponse;
import com.hcmute.fit.toeicrise.models.entities.UserTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTestRepository extends JpaRepository<UserTest, Long>, JpaSpecificationExecutor<UserTest> {
    @Query("SELECT ut FROM UserTest ut " +
            "JOIN FETCH ut.userAnswers ua " +
            "JOIN FETCH ua.question " +
            "WHERE ut.id = :id")
    Optional<UserTest> findByIdWithAnswersAndQuestions(@Param("id") Long id);

    @Query("SELECT ut " +
            "FROM UserTest ut " +
            "LEFT JOIN FETCH ut.test t " +
            "LEFT JOIN FETCH ut.userAnswers ua " +
            "LEFT JOIN FETCH ua.question q " +
            "LEFT JOIN FETCH q.questionGroup qg " +
            "LEFT JOIN FETCH qg.part p " +
            "WHERE ut.id = :id AND ut.user.account.email = :email")
    Optional<UserTest> findUserTestById(@Param("id") Long id, @Param("email") String email);

    @Query("SELECT new com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestHistoryResponse(" +
            "ut.id, t.name, ut.createdAt, ut.parts, ut.correctAnswers,ut.totalQuestions, ut.totalScore, ut.timeSpent) " +
            "FROM Test t " +
            "INNER JOIN UserTest ut ON t.id = ut.test.id " +
            "WHERE t.id = :id AND ut.user.account.email = :email " +
            "ORDER BY ut.createdAt DESC ")
    List<LearnerTestHistoryResponse> getLearnerTestHistoryByTest_IdAndUser_Email(@Param("id") Long testId, @Param("email") String email);
}
