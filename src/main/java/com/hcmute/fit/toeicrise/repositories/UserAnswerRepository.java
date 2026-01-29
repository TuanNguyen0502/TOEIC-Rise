package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    @Query("SELECT ua " +
            "FROM UserAnswer ua " +
            "LEFT JOIN FETCH ua.question q " +
            "WHERE ua.userTest.id IN :userTestIds")
    List<UserAnswer> findByUserTestIdInWithQuestion (@Param("userTestIds") List<Long> userTestIds);
}
