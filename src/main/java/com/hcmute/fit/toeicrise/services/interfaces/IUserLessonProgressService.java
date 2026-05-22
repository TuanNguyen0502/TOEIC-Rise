package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.learningpath.UserLessonProgressUpsertRequest;
import com.hcmute.fit.toeicrise.dtos.responses.learningpath.LessonWithProgressResponse;
import com.hcmute.fit.toeicrise.models.entities.Lesson;
import com.hcmute.fit.toeicrise.models.entities.UserLessonProgress;

import java.util.List;

public interface IUserLessonProgressService {
    void upsertProgress(String email, UserLessonProgressUpsertRequest request);
    List<LessonWithProgressResponse> getLessonProgress(String email, List<Lesson> lessons);
}
