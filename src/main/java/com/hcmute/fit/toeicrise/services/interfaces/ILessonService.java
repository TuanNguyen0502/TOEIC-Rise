package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LessonCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LessonReorderRequest;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LessonUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learningpath.LessonDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learningpath.LessonResponseForLearner;
import com.hcmute.fit.toeicrise.models.entities.Lesson;
import com.hcmute.fit.toeicrise.models.enums.ELessonLevel;

import java.util.List;

public interface ILessonService {
    LessonDetailResponse createLesson(String slug, String email, LessonCreateRequest request);
    LessonDetailResponse updateLesson(Long id, LessonUpdateRequest request);
    Lesson getLessonById(Long id);
    List<Lesson> getAllLessonsByIds(List<Long> ids);
    void reorderLesson(LessonReorderRequest request);
    void setLessonActive(Long id, Boolean active);
    Lesson getLessonWithLearningPathId(Long id);
    LessonDetailResponse getLesson(Long id, String email);
    LessonResponseForLearner getLessonsResponsesForLearner(Lesson lesson);
    PageResponse getLessonsForPage(String learningPathSlug, String name, ELessonLevel level, int page, int size, String sortBy, String direction);
}
