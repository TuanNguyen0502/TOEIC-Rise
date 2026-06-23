package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.learningpath.ItemRequest;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LessonCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LessonReorderRequest;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LessonUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learningpath.LessonDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learningpath.LessonResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learningpath.LessonResponseForLearner;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.*;
import com.hcmute.fit.toeicrise.models.enums.ELessonLevel;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.LessonMapper;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.repositories.LearningPathRepository;
import com.hcmute.fit.toeicrise.repositories.LessonRepository;
import com.hcmute.fit.toeicrise.repositories.UserLessonProgressRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.LessonSpecification;
import com.hcmute.fit.toeicrise.services.interfaces.ILessonService;
import com.hcmute.fit.toeicrise.services.interfaces.ITagService;
import com.hcmute.fit.toeicrise.services.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements ILessonService {
    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final IUserService userService;
    private final ITagService tagService;
    private final LearningPathRepository learningPathRepository;
    private final UserLessonProgressRepository userLessonProgressRepository;
    private final PageResponseMapper pageResponseMapper;

    @Override
    public LessonDetailResponse createLesson(String slug, String email, LessonCreateRequest request) {
        LearningPath learningPath = learningPathRepository.findBySlug(slug).orElse(null);

        if (learningPath == null)
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Learning path");
        if(!request.getSlug().equals(learningPath.getSlug())){
            Lesson existedLesson = lessonRepository.findBySlugAndLearningPathId(request.getSlug(), learningPath.getId()).orElse(null);
            if (existedLesson != null)
                throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Lesson's slug");
        }
        Integer orderIndex = lessonRepository.findTopByOrderIndexAndLearningPathId(learningPath.getId());

        Lesson lesson = lessonMapper.toEntity(request);
        lesson.setLearningPath(learningPath);
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setOrderIndex(orderIndex+1);
        lesson.setIsActive(true);

        return lessonMapper.toDetailResponse(lessonRepository.save(lesson));
    }

    @Transactional
    @Override
    public LessonDetailResponse updateLesson(Long id, LessonUpdateRequest request) {
        Lesson lesson = getLessonById(id);
        if(!request.getSlug().equals(lesson.getSlug())){
            Lesson existedLesson = lessonRepository.findBySlugAndLearningPathId(request.getSlug(), lesson.getLearningPath().getId()).orElse(null);
            if (existedLesson != null)
                throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Lesson's slug");
        }
        if (request.getPractice() != null && !request.getPractice().isBlank()) {
            String practiceValue = request.getPractice().trim();
            if (practiceValue.matches("^\\d+$")) {
                request.setPractice(practiceValue);
            } else {
                Tag tag = tagService.getTagByName(practiceValue);
                request.setPractice(String.valueOf(tag.getId()));
            }
        }

        lesson = lessonMapper.toEntity(request, lesson);
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

    @Transactional
    @Override
    public void reorderLesson(LessonReorderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty())
            return;
        LearningPath learningPath = learningPathRepository.findBySlug(request.getLearningPathSlug())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Learning path"));
        List<Lesson> lessons = learningPath.getLessons();
        List<Lesson> updatedLessons = new ArrayList<>();

        for(ItemRequest item : request.getItems()) {
            Long targetId = item.getLessonId();
            int newOrderIndex = item.getOrderIndex();

            Lesson moveLesson = lessons.stream()
                    .filter(l -> l.getId().equals(targetId))
                    .findFirst().orElse(null);

            if (moveLesson == null || moveLesson.getOrderIndex() == newOrderIndex)
                continue;

            int temporaryIndex = -(newOrderIndex + 100000);
            moveLesson.setOrderIndex(temporaryIndex);
            updatedLessons.add(moveLesson);
        }
        if (updatedLessons.isEmpty())
            return;

        lessonRepository.saveAll(updatedLessons);
        lessonRepository.flush();

        for (ItemRequest item : request.getItems()) {
            Long targetId = item.getLessonId();
            int newOrderIndex = item.getOrderIndex();

            updatedLessons.stream().filter(l -> l.getId().equals(targetId))
                    .findFirst().ifPresent(lesson -> lesson.setOrderIndex(newOrderIndex));
        }
        lessonRepository.saveAll(updatedLessons);
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
    public LessonDetailResponse getLesson(String slug, String email, String learningPathSlug) {
        User user = userService.getUserByEmail(email);
        Lesson lesson = lessonRepository.findByLearningPathBySlug(slug, learningPathSlug).orElseThrow(()
                -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Lesson"));
        UserLessonProgress userLessonProgress = userLessonProgressRepository.findByUserIdAndLessonId(user.getId(), lesson.getId()).orElse(null);

        if (!Boolean.TRUE.equals(lesson.getIsActive()) || !Boolean.TRUE.equals(lesson.getLearningPath().getIsActive()))
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Lesson");

        LessonDetailResponse response = lessonMapper.toDetailResponse(lesson);
        if (lesson.getPractice() != null && !lesson.getPractice().isBlank()) {
            long tagId = Long.parseLong(lesson.getPractice());
            Tag tag = tagService.getTagById(tagId);
            response.setPractice(tag.getName());
        }
        if (lesson.getSlug() == null)
            response.setSlug(null);
        if (userLessonProgress != null)
            response.setNotice(userLessonProgress.getNotice());
        return response;
    }

    @Override
    public LessonDetailResponse getLesson(Long id, String email) {
        Lesson lesson = getLessonWithLearningPathId(id);
        LessonDetailResponse response = lessonMapper.toDetailResponse(lesson);

        if(lesson.getPractice() != null && !lesson.getPractice().isBlank()){
            long tagId = Long.parseLong(lesson.getPractice());
            Tag tag = tagService.getTagById(tagId);
            response.setPractice(tag.getName());
        }
        return response;
    }

    @Override
    public LessonResponseForLearner getLessonsResponsesForLearner(Lesson lesson) {
        LessonResponseForLearner response = lessonMapper.toLessonResponseForLearner(lesson);
        if (lesson.getPractice() != null && !lesson.getPractice().trim().isEmpty()) {
            String practiceValue = lesson.getPractice().trim();
            if (practiceValue.matches("^\\d+$")) {
                long tagId = Long.parseLong(practiceValue);
                Tag tag = tagService.getTagById(tagId);
                response.setPractice(tag.getName());
            }
        }
        return response;
    }

    @Override
    public PageResponse getLessonsForPage(String learningPathSlug, String name, ELessonLevel level, int page, int size, String sortBy, String direction) {
        Specification<Lesson> specification = (_, _, cb) -> cb.conjunction();
        return getLessonResponses(learningPathSlug, name, level, page,size, sortBy, direction, specification);
    }

    @Override
    public Lesson getLessonOrderByOrderIndexDesc(Long learningPathId, ELessonLevel level) {
        return lessonRepository.findFirstByLearningPathIdAndLevelOrderByOrderIndexDesc(learningPathId, level).orElse(null);
    }

    private PageResponse getLessonResponses(String learningPathSlug, String name, ELessonLevel level, int page, int size, String sortBy, String direction, Specification<Lesson> specification) {
        if (learningPathSlug != null)
            specification = specification.and(LessonSpecification.learningPathSlugEquals(learningPathSlug));
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
