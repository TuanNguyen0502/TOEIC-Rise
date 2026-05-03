package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.models.entities.LearningPath;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.entities.UserLearningPath;
import com.hcmute.fit.toeicrise.models.entities.UserTest;
import com.hcmute.fit.toeicrise.models.enums.ELessonLevel;
import com.hcmute.fit.toeicrise.models.enums.ETestType;
import com.hcmute.fit.toeicrise.repositories.UserLearningPathRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IUserLearningPathService;
import com.hcmute.fit.toeicrise.services.interfaces.IUserTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserLearningPathServiceImpl implements IUserLearningPathService {
    private final UserLearningPathRepository userLearningPathRepository;
    private final IUserTestService userTestService;

    @Override
    public UserLearningPath getUserLearningPath(Long userId, Long pathId) {
        return userLearningPathRepository.findByUserIdAndLearningPathId(userId, pathId).orElse(null);
    }

    @Override
    public UserLearningPath createUserLearningPath(User user, LearningPath learningPath, ELessonLevel level) {
        UserLearningPath userLearningPath = UserLearningPath.builder()
                .user(user)
                .learningPath(learningPath)
                .selectedAt(LocalDateTime.now())
                .level(level)
                .build();
        return userLearningPathRepository.save(userLearningPath);
    }

    @Override
    public ELessonLevel getLessonLevel(String email, ETestType testType) {
        UserTest latest = userTestService.getLastestUserTest(email, testType);
        if (latest == null)
            return ELessonLevel.BEGINNER;

        return switch (testType) {
            case LISTENING_AND_READING -> {
                if (latest.getTotalScore() < 300)
                    yield ELessonLevel.BEGINNER;
                if (latest.getTotalScore() < 600)
                    yield ELessonLevel.INTERMEDIATE;
                yield ELessonLevel.ADVANCED;
            }
            case SPEAKING, WRITING -> {
                if (latest.getTotalScore() < 100)
                    yield ELessonLevel.BEGINNER;
                if (latest.getTotalScore() < 160)
                    yield ELessonLevel.INTERMEDIATE;
                yield ELessonLevel.ADVANCED;
            }
        };
    }

}
