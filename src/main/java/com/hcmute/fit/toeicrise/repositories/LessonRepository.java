package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    @Query("SELECT l, p " +
            "FROM Lesson l " +
            "LEFT JOIN LearningPath p " +
            "WHERE l.id =:id")
    Optional<Lesson> findWithLearningPathById(@Param("id") Long id);
}
