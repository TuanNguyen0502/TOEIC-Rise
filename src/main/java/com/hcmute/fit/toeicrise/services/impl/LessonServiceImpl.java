package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.commons.utils.CloudinaryUtil;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.ItemRequest;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LessonCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LessonReorderRequest;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LessonUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learningpath.LessonDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learningpath.LessonResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learningpath.LessonResponseForLearner;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.LearningPath;
import com.hcmute.fit.toeicrise.models.entities.Lesson;
import com.hcmute.fit.toeicrise.models.enums.ELessonLevel;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.LessonMapper;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.repositories.LessonRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.LessonSpecification;
import com.hcmute.fit.toeicrise.services.interfaces.ILessonService;
import com.hcmute.fit.toeicrise.services.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final CloudinaryUtil cloudinaryUtil;
    private final IUserService userService;
    private final PageResponseMapper pageResponseMapper;

    private static final int OFFSET = 1_000_000;

    @Override
    public LessonDetailResponse createLesson(LessonCreateRequest request, LearningPath path) {
        Lesson lesson = lessonMapper.toEntity(request);
        lesson.setLearningPath(path);
        lesson.setVideoUrl(request.getVideoUrl());

        return lessonMapper.toDetailResponse(lessonRepository.save(lesson));
    }

    @Transactional
    @Override
    public LessonDetailResponse updateLesson(Long id, LessonUpdateRequest request) {
        Lesson lesson = getLessonById(id);
        String oldUrl = lesson.getVideoUrl();
        String newUrl = request.getVideoUrl();

        if (newUrl != null && !newUrl.isBlank()) {
            if (oldUrl != null && cloudinaryUtil.isCloudinaryUrl(oldUrl) && !oldUrl.equals(newUrl))
                cloudinaryUtil.deleteFile(oldUrl);
        } else newUrl = oldUrl;

        lesson = lessonMapper.toEntity(request, lesson);
        lesson.setVideoUrl(newUrl);
        return lessonMapper.toDetailResponse(lessonRepository.save(lesson));
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
            lesson.setOrderIndex(-(item.getOrderIndex() + OFFSET));
        }
        lessonRepository.saveAll(lessons);
        lessonRepository.flush();
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
    public LessonDetailResponse getLessonForLearner(Long id, String email) {
        userService.getUserByEmail(email);
        Lesson lesson = getLessonWithLearningPathId(id);

        if (!Boolean.TRUE.equals(lesson.getIsActive()) || !Boolean.TRUE.equals(lesson.getLearningPath().getIsActive()))
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Lesson");

        return lessonMapper.toDetailResponse(lesson);
    }

    @Override
    public LessonResponseForLearner getLessonsResponsesForLearner(Lesson lesson) {
        return lessonMapper.toLessonResponseForLearner(lesson);
    }

    @Override
    public PageResponse getLessonsForPage(Long learningPathId, String name, ELessonLevel level, int page, int size, String sortBy, String direction) {
        Specification<Lesson> specification = (_, _, cb) -> cb.conjunction();
        return getLessonResponses(learningPathId, name, level, page,size, sortBy, direction, specification);
    }

    private PageResponse getLessonResponses(Long learningPathId, String name, ELessonLevel level, int page, int size, String sortBy, String direction, Specification<Lesson> specification) {
        if (learningPathId != null)
            specification = specification.and(LessonSpecification.learningPathIdEquals(learningPathId));
        if (name != null && !name.trim().isEmpty())
            specification = specification.and(LessonSpecification.nameContains(name));
        if (level != null)
            specification = specification.and(LessonSpecification.lessonLevelEquals(level));

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<LessonResponse> lessonResponses = lessonRepository.findAll(specification, pageable).map(lessonMapper::toResponse);
        return pageResponseMapper.toPageResponse(lessonResponses);
    }
}
