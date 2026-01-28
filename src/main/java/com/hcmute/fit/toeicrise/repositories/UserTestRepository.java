package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestHistoryResponse;
import com.hcmute.fit.toeicrise.dtos.responses.statistic.ScoreDistInsightResponse;
import com.hcmute.fit.toeicrise.dtos.responses.statistic.TestModeInsightResponse;
import com.hcmute.fit.toeicrise.models.entities.UserTest;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserTestRepository extends JpaRepository<UserTest, Long>, JpaSpecificationExecutor<UserTest> {
    @EntityGraph(attributePaths = {"userAnswers", "userAnswers.question", "userAnswers.question.tags"})
    @Query("SELECT ut FROM UserTest ut WHERE ut.id = :id")
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

    @Query("SELECT DISTINCT ut " +
            "FROM UserTest ut " +
            "LEFT JOIN FETCH ut.test t " +
            "LEFT JOIN FETCH ut.userAnswers ua " +
            "LEFT JOIN FETCH ua.question q " +
            "LEFT JOIN FETCH q.questionGroup qg " +
            "LEFT JOIN FETCH qg.part p " +
            "WHERE ut.user.account.email = :email AND ut.createdAt >= :days AND t.status = :status")
    List<UserTest> findAllAnalysisResult(@Param("email") String email, @Param("days") LocalDateTime days, @Param("status")ETestStatus status);

    @Query("SELECT new com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestHistoryResponse(" +
            "ut.id, t.name, ut.createdAt, ut.parts, ut.correctAnswers,ut.totalQuestions, ut.totalScore, ut.timeSpent) " +
            "FROM UserTest ut " +
            "JOIN ut.test t " +
            "WHERE t.id = :id AND ut.user.account.email = :email " +
            "ORDER BY ut.createdAt DESC ")
    List<LearnerTestHistoryResponse> getLearnerTestHistoryByTest_IdAndUser_Email(@Param("id") Long testId, @Param("email") String email);

    Optional<UserTest> findFirstByOrderByCreatedAtDesc();

    List<UserTest> findByUser_Account_EmailAndTest_StatusAndTotalScoreIsNotNullOrderByCreatedAtDesc(@Param("email") String email, Pageable pageable, ETestStatus status);

    @Query("SELECT FUNCTION('DATE', ut.createdAt), COUNT(ut) " +
            "FROM UserTest ut " +
            "WHERE ut.createdAt >= :start AND ut.createdAt < :end " +
            "GROUP BY FUNCTION('DATE', ut.createdAt) " +
            "ORDER BY FUNCTION('DATE', ut.createdAt)")
    List<Object[]> getActivityTrend(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT new com.hcmute.fit.toeicrise.dtos.responses.statistic.TestModeInsightResponse(" +
            "COALESCE(SUM(CASE WHEN ut.totalScore IS NOT NULL THEN 1 ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN ut.totalScore IS NULL THEN 1 ELSE 0 END), 0)) " +
            "FROM UserTest ut " +
            "WHERE ut.createdAt >= :start AND ut.createdAt < :end ")
    TestModeInsightResponse countUserTestByMode(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT new com.hcmute.fit.toeicrise.dtos.responses.statistic.ScoreDistInsightResponse(" +
            "COALESCE(SUM(CASE WHEN ut.totalScore BETWEEN 0 AND 200 THEN 1 ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN ut.totalScore BETWEEN 201 AND 450 THEN 1 ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN ut.totalScore BETWEEN 451 AND 750 THEN 1 ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN ut.totalScore BETWEEN 751 AND 990 THEN 1 ELSE 0 END), 0)) " +
            "FROM UserTest ut " +
            "WHERE ut.createdAt >= :start AND ut.createdAt < :end")
    ScoreDistInsightResponse countUserTestByScore(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
