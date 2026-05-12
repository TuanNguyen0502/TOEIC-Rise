package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.Lesson;
import com.hcmute.fit.toeicrise.models.enums.ELessonLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long>, JpaSpecificationExecutor<Lesson> {
    @Query("SELECT l " +
            "FROM Lesson l " +
            "LEFT JOIN FETCH l.learningPath " +
            "WHERE l.id =:id")
    Optional<Lesson> findWithLearningPathById(@Param("id") Long id);

    @Query("SELECT l " +
            "FROM Lesson l " +
            "LEFT JOIN FETCH l.learningPath " +
            "WHERE l.slug =:slug")
    Optional<Lesson> findByLearningPathBySlug(@Param("slug") String slug);

    Optional<Lesson> findFirstByLearningPathIdAndLevelOrderByOrderIndexDesc(Long learningPathId, ELessonLevel level);
}
