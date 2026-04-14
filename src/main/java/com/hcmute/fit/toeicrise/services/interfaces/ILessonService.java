package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LessonCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LessonReorderRequest;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LessonUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.learningpath.LessonResponse;
import com.hcmute.fit.toeicrise.models.entities.LearningPath;
import com.hcmute.fit.toeicrise.models.entities.Lesson;

import java.util.List;

public interface ILessonService {
    LessonResponse createLesson(LessonCreateRequest request, LearningPath learningPath);
    LessonResponse updateLesson(Long id, LessonUpdateRequest request);
    Lesson getLessonById(Long id);
    List<Lesson> getAllLessonsByIds(List<Long> ids);
    void reorderLesson(LessonReorderRequest request, LearningPath learningPath);
    void setLessonActive(Long id, Boolean active);
    Lesson getLessonWithLearningPathId(Long id);
    LessonResponse getLessonForLearner(Long id, String email);
}
