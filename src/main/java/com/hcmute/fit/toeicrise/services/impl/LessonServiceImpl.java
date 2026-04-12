package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.ItemRequest;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LessonCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LessonReorderRequest;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LessonUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.learningpath.LessonResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.LearningPath;
import com.hcmute.fit.toeicrise.models.entities.Lesson;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.LessonMapper;
import com.hcmute.fit.toeicrise.repositories.LessonRepository;
import com.hcmute.fit.toeicrise.services.interfaces.ICloudinaryService;
import com.hcmute.fit.toeicrise.services.interfaces.ILessonService;
import com.hcmute.fit.toeicrise.services.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements ILessonService {
    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final ICloudinaryService cloudinaryService;
    private final IUserService userService;

    private static final int OFFSET = 1_000_000;

    @Override
    public LessonResponse createLesson(LessonCreateRequest request, MultipartFile file, LearningPath path) {
        Lesson lesson = lessonMapper.toEntity(request);
        lesson.setLearningPath(path);

        String resolvedVideoUrl = cloudinaryService.processMediaFile(file, request.getVideoUrl(), null);
        if (resolvedVideoUrl == null)
            throw new AppException(ErrorCode.INVALID_REQUEST, MessageConstant.EITHER_MULTIPLE_OR_URL_REQUIRED);
        lesson.setVideoUrl(resolvedVideoUrl);

        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }

    @Transactional
    @Override
    public LessonResponse updateLesson(Long id, LessonUpdateRequest request, MultipartFile file) {
        Lesson lesson = getLessonById(id);
        String url = cloudinaryService.processMediaFile(file, request.getVideoUrl(), lesson.getVideoUrl());

        lesson = lessonMapper.toEntity(request);
        lesson.setVideoUrl(url);
        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }

    @Override
    public Lesson getLessonById(Long id) {
        return lessonRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Lesson"));
    }

    @Override
    public List<Lesson> getAllLessonsByIds(List<Long> ids) {
        return lessonRepository.findAllById(ids);
    }

    @Override
    public void reorderLesson(LessonReorderRequest request, LearningPath path) {
        List<Long> lessonIds = request.getItems().stream().map(ItemRequest::getLessonId).toList();
        List<Lesson> lessons = getAllLessonsByIds(lessonIds);
        Map<Long, Lesson> byId = lessons.stream().collect(Collectors.toMap(Lesson::getId, Function.identity()));

        for (Lesson lesson : lessons) {
            if (!Objects.equals(lesson.getLearningPath().getId(), path.getId()))
                throw new AppException(ErrorCode.INVALID_REQUEST, MessageConstant.LESSON_DO_NOT_BELONG_LEARNING_PATH);
        }
        if (byId.size() != lessonIds.size())
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Lesson");

        for (ItemRequest item : request.getItems()) {
            Lesson lesson = byId.get(item.getLessonId());
            lesson.setOrderIndex(item.getOrderIndex() + OFFSET);
        }
        lessonRepository.saveAll(lessons);

        for (ItemRequest item : request.getItems()) {
            Lesson lesson = byId.get(item.getLessonId());
            lesson.setOrderIndex(item.getOrderIndex());
        }
        lessonRepository.saveAll(lessons);
    }

    @Transactional
    @Override
    public void setLessonActive(Long id, Boolean active) {
        Lesson lesson = getLessonById(id);
        lesson.setIsActive(active);
        lessonRepository.save(lesson);
    }

    @Override
    public Lesson getLessonWithLearningPathId(Long id) {
        return lessonRepository.findWithLearningPathById(id).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Lesson"));
    }

    @Override
    public LessonResponse getLessonForLearner(Long id, String email) {
        userService.getUserByEmail(email);
        Lesson lesson = getLessonWithLearningPathId(id);

        if (!Boolean.TRUE.equals(lesson.getIsActive()) || !Boolean.TRUE.equals(lesson.getLearningPath().getIsActive()))
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Lesson");

        return lessonMapper.toResponse(lesson);
    }
}
