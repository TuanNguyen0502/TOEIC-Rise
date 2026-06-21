package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.learningpath.*;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learningpath.*;
import com.hcmute.fit.toeicrise.models.entities.LearningPath;
import com.hcmute.fit.toeicrise.models.enums.ELessonLevel;
import com.hcmute.fit.toeicrise.models.enums.ETestType;

import java.util.List;

public interface ILearningPathService {
    PageResponse getAllLearningPaths(String name, int page, int size, String sortBy, String direction);
    void createLearningPath(LearningPathCreateRequest request);
    void updateLearningPath(Long learningPathId, LearningPathUpdateRequest request);
    LearningPath getLearningPath(Long learningPathId);
    LearningPathDetailResponseForLearner getLearningPathDetailForLearner(String email, String learningPathSlug);
    List<LearningPathSummaryResponse> listActiveLearningPaths();
    LearningPathResponse getLearningPathResponse(Long learningPathId);
    LearningPathDetailResponse getLearningPathDetail(String learningPathSlug, String name, ELessonLevel level, int page, int size, String sortBy, String direction);
    LessonLevelResponse getLearningPathLevel(String learningPathSlug, String email, ETestType testType);
    void createUserLearningPath(String email, String learningPathSlug, ELessonLevel level);
}
