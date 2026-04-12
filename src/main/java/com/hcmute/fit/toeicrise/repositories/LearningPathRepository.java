package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.LearningPath;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LearningPathRepository extends JpaRepository<LearningPath, Long> {
    @NotNull
    @EntityGraph(attributePaths = {"lessons"})
    Page<LearningPath> findAll(Specification<LearningPath> spec, @NotNull Pageable pageable);

    @Query("SELECT lp " +
            "FROM LearningPath lp " +
            "LEFT JOIN lp.lessons " +
            "WHERE lp.id =:id")
    Optional<LearningPath> findLearningPathWithLessonsById(@Param("id") Long id);
}
