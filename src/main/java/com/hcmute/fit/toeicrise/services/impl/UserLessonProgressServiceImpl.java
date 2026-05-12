package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.learningpath.UserLessonProgressUpsertRequest;
import com.hcmute.fit.toeicrise.dtos.responses.learningpath.LessonWithProgressResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Lesson;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.entities.UserLessonProgress;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.UserLessonProgressMapper;
import com.hcmute.fit.toeicrise.repositories.UserLessonProgressRepository;
import com.hcmute.fit.toeicrise.services.interfaces.ILessonService;
import com.hcmute.fit.toeicrise.services.interfaces.IUserLessonProgressService;
import com.hcmute.fit.toeicrise.services.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserLessonProgressServiceImpl implements IUserLessonProgressService {
    private final IUserService userService;
    private final ILessonService lessonService;
    private final UserLessonProgressRepository userLessonProgressRepository;
    private final UserLessonProgressMapper userLessonProgressMapper;

    @Transactional
    @Override
    public void upsertProgress(String email, UserLessonProgressUpsertRequest request) {
        User user = userService.getUserByEmail(email);

        Lesson lesson = lessonService.getLessonWithLearningPathId(request.getLessonId());
        if (!Boolean.TRUE.equals(lesson.getIsActive()) || !Boolean.TRUE.equals(lesson.getLearningPath().getIsActive()))
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Lesson");

        UserLessonProgress progress = userLessonProgressRepository.findByUserIdAndLessonId(user.getId(), lesson.getId())
                .orElseGet(() -> UserLessonProgress.builder()
                        .user(user)
                        .lesson(lesson)
                        .progressPercentage(0.0)
                        .lastWatchedTimeMs(0L)
                        .isCompleted(false)
                        .notice(request.getNotice())
                        .build());

        double pct = request.getProgressPercentage();
        if (lesson.getVideoUrl() == null || lesson.getVideoUrl().isEmpty())
            pct = 100.0;
        if (progress.getProgressPercentage() < 100)
            progress.setProgressPercentage(pct);
        progress.setLastWatchedTimeMs(request.getLastWatchedTimeMs());
        progress.setIsCompleted(pct >= 80.0);
        progress.setNotice(request.getNotice());
        userLessonProgressRepository.save(progress);
    }

    @Override
    public List<LessonWithProgressResponse> getLessonProgress(String email, List<Lesson> lessons) {
        if (lessons == null || lessons.isEmpty()) return List.of();

        User user = userService.getUserByEmail(email);
        List<Long> lessonIds = lessons.stream().map(Lesson::getId).toList();

        Map<Long, UserLessonProgress> progressByLessonId = userLessonProgressRepository
                .findAllByUserIdAndLessonIdIn(user.getId(), lessonIds)
                .stream()
                .collect(Collectors.toMap(p -> p.getLesson().getId(), Function.identity(), (a, _) -> a));

        return lessons.stream()
                .sorted(Comparator.comparing(Lesson::getOrderIndex))
                .map(lesson -> {
                    UserLessonProgress progress = progressByLessonId.get(lesson.getId());
                    if (progress == null) {
                        return LessonWithProgressResponse.builder()
                                .lesson(lessonService.getLessonsResponsesForLearner(lesson))
                                .build();
                    }

                    LessonWithProgressResponse res = userLessonProgressMapper.toLessonWithProgressResponse(progress);
                    res.setLesson(lessonService.getLessonsResponsesForLearner(lesson));
                    return res;
                })
                .toList();
    }
}
