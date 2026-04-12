package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LearningPathCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LearningPathUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LessonCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LessonReorderRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learningpath.LearningPathDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learningpath.LessonResponse;
import com.hcmute.fit.toeicrise.models.entities.LearningPath;
import org.springframework.web.multipart.MultipartFile;

public interface ILearningPathService {
    PageResponse getAllLearningPaths(String name, int page, int size, String sortBy, String direction);
    LearningPathDetailResponse getLearningPathDetail(Long learningPathId);
    void createLearningPath(LearningPathCreateRequest request);
    void updateLearningPath(Long learningPathId, LearningPathUpdateRequest request);
    LearningPath getLearningPath(Long learningPathId);
    LessonResponse createLesson(Long learningPathId, LessonCreateRequest request, MultipartFile file);
    LearningPathDetailResponse getLearningPathDetailForLearner(String email, Long learningPathId);
    void reorderLessons(Long learningPathId, LessonReorderRequest request);
    PageResponse listLearningPaths(Boolean isActive, int page, int size, String sortBy, String direction);
    LearningPathDetailResponse getLearningPathDetailForAdmin(Long learningPathId);
    PageResponse listActiveLearningPaths(int page, int size, String sortBy, String direction);
}
