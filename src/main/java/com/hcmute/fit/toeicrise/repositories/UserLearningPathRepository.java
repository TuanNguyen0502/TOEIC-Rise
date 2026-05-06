package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.UserLearningPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLearningPathRepository extends JpaRepository<UserLearningPath, Long> {
    Optional<UserLearningPath> findByUserIdAndLearningPathId(Long userId, Long learningPathId);
    Optional<UserLearningPath> findByUserIdAndLearningPathSlug(Long userId, String learningPathSlug);
}
