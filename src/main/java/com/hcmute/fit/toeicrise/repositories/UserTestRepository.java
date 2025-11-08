package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.UserTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTestRepository extends JpaRepository<UserTest, Long> {
    @Query("SELECT ut FROM UserTest ut " +
            "JOIN FETCH ut.userAnswers ua " +
            "JOIN FETCH ua.question " +
            "WHERE ut.id = :id")
    Optional<UserTest> findByIdWithAnswersAndQuestions(@Param("id") Long id);

    @Query("""
                SELECT DISTINCT ut
                FROM UserTest ut
                JOIN FETCH ut.userAnswers ua
                JOIN FETCH ua.question q
                LEFT JOIN FETCH q.tags t
                WHERE ut.id = :id
            """)
    Optional<UserTest> findByIdWithAnswersQuestionsAndTags(@Param("id") Long id);
}
