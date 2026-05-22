package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.models.entities.LearningPath;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.entities.UserLearningPath;
import com.hcmute.fit.toeicrise.models.enums.ELessonLevel;
import com.hcmute.fit.toeicrise.models.enums.ETestType;

public interface IUserLearningPathService {
    UserLearningPath getUserLearningPath(Long userId, Long pathId);
    UserLearningPath createUserLearningPath(User user, LearningPath learningPath, ELessonLevel level);
    ELessonLevel getLessonLevel(String email, ETestType testType);
    UserLearningPath getUserLearningPath(Long userId, String learningPathSlug);
    void saveUserLearningPath(UserLearningPath userLearningPath);
}
