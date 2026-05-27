package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.learningpath.*;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learningpath.*;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.LearningPath;
import com.hcmute.fit.toeicrise.models.entities.Lesson;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.entities.UserLearningPath;
import com.hcmute.fit.toeicrise.models.enums.ELessonLevel;
import com.hcmute.fit.toeicrise.models.enums.ETestType;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.LearningPathMapper;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.repositories.LearningPathRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.LearningPathSpecification;
import com.hcmute.fit.toeicrise.services.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LearningPathServiceImpl implements ILearningPathService {
    private final LearningPathRepository learningPathRepository;
    private final IUserLearningPathService userLearningPathService;
    private final IUserService userService;
    private final IUserLessonProgressService userLessonProgressService;
    private final ILessonService lessonService;
    private final LearningPathMapper learningPathMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    public PageResponse getAllLearningPaths(String name, int page, int size, String sortBy, String direction) {
        Specification<LearningPath> specification = (_, _, cb) -> cb.conjunction();
        return getLearningPathResponses(name, page, size, sortBy, direction, specification);
    }

    @Override
    public PageResponse listLearningPaths(Boolean isActive, int page, int size, String sortBy, String direction) {
        Specification<LearningPath> specification = (_, _, cb) -> cb.conjunction();
        specification = specification.and(LearningPathSpecification.hasIsActive(isActive));
        return getLearningPathResponses(null, page, size, sortBy, direction, specification);
    }

    @Override
    @Transactional
    public void createLearningPath(LearningPathCreateRequest request) {
        LearningPath learningPath = learningPathRepository.findBySlug(request.getSlug()).orElse(null);
        if (learningPath != null)
            throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Slug");

        LearningPath path = learningPathMapper.toEntity(request);
        path.setIsActive(true);
        path.setTestType(request.getTestType());
        learningPathRepository.save(path);
    }

    @Transactional
    @Override
    public void updateLearningPath(Long learningPathId, LearningPathUpdateRequest request) {
        LearningPath path = getLearningPath(learningPathId);
        LearningPath learningPath = learningPathRepository.findBySlug(request.getSlug()).orElse(null);

        if (learningPath != null && !learningPath.getId().equals(path.getId()))
            throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Slug");

        path.setName(request.getName());
        path.setSlug(request.getSlug());
        path.setDescription(request.getDescription());
        path.setTestType(request.getTestType());
        path.setIsActive(request.getIsActive());
        if (Boolean.FALSE.equals(request.getIsActive()) && path.getLessons() != null) {
            path.getLessons().forEach(lesson -> lesson.setIsActive(false));
        }
        learningPathRepository.save(path);
    }

    @Override
    public LearningPath getLearningPath(Long learningPathId) {
        return learningPathRepository.findLearningPathWithLessonsById(learningPathId).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Learning path"));
    }

    @Override
    public PageResponse listActiveLearningPaths(int page, int size, String sortBy, String direction) {
        return listLearningPaths(true, page, size, sortBy, direction);
    }

    @Override
    public LearningPathResponse getLearningPathResponse(Long learningPathId) {
        LearningPath learningPath = learningPathRepository.findLearningPathWithLessonsById(learningPathId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Learning path"));
        return learningPathMapper.toLearningPathResponse(learningPath);
    }

    @Override
    public LearningPathDetailResponse getLearningPathDetail(String learningPathSlug, String name, ELessonLevel level, int page, int size, String sortBy, String direction) {
        LearningPath path = learningPathRepository.findBySlug(learningPathSlug)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Learning Path"));

        LearningPathDetailResponse response = learningPathMapper.toLearningPathDetailResponse(path);
        if (path.getLessons() == null) response.setLessons((PageResponse) List.of());
        else response.setLessons(lessonService.getLessonsForPage(learningPathSlug, name, level, page, size, sortBy, direction));
        return response;
    }

    @Override
    public LessonLevelResponse getLearningPathLevel(String learningPathSlug, String email, ETestType testType) {
        User user = userService.getUserByEmail(email);
        UserLearningPath userLearningPath = userLearningPathService.getUserLearningPath(user.getId(), learningPathSlug);
        ELessonLevel level = userLearningPath == null ? null : userLearningPath.getLevel();
        ELessonLevel chooseLevel = userLearningPathService.getLessonLevel(email, testType);
        return LessonLevelResponse.builder()
                .currentLevel(level)
                .chooseLevel(chooseLevel)
                .build();
    }

    @Override
    public void createUserLearningPath(String email, String learningPathSlug, ELessonLevel level) {
        User user = userService.getUserByEmail(email);
        LearningPath path = learningPathRepository.findLearningPathWithLessonsBySlug(learningPathSlug)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Learning Path"));
        if (!Boolean.TRUE.equals(path.getIsActive()))
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Learning Path");

        userLearningPathService.createUserLearningPath(user, path, level);
    }

    @Override
    public LearningPathDetailResponseForLearner getLearningPathDetailForLearner(String email, String learningPathSlug) {
        User user = userService.getUserByEmail(email);
        LearningPath path = learningPathRepository.findLearningPathWithLessonsBySlug(learningPathSlug)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Learning Path"));
        if (!Boolean.TRUE.equals(path.getIsActive()))
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Learning Path");

        UserLearningPath userLearningPath = userLearningPathService.getUserLearningPath(user.getId(), learningPathSlug);
        ELessonLevel targetLevel = userLearningPath.getLevel();
        List<Lesson> lessonsForLearner =
                path.getLessons() == null ? List.of() : path.getLessons().stream()
                        .filter(l -> Boolean.TRUE.equals(l.getIsActive()))
                        .filter(l -> l.getLevel().ordinal() <= targetLevel.ordinal())
                        .sorted(Comparator.comparingInt(l -> l.getOrderIndex() == null ? Integer.MAX_VALUE : l.getOrderIndex()))
                        .toList();

        LearningPathDetailResponseForLearner response = learningPathMapper.toLearningPathDetailResponseForLearner(path);
        response.setLessons(userLessonProgressService.getLessonProgress(email, lessonsForLearner));
        return response;
    }

    private PageResponse getLearningPathResponses(String name, int page, int size, String sortBy, String direction, Specification<LearningPath> specification) {
        if (name != null && !name.trim().isEmpty())
            specification = specification.and(LearningPathSpecification.containsName(name));

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<LearningPathSummaryResponse> pages = learningPathRepository.findAll(specification, pageRequest)
                .map(lp -> learningPathMapper.toSummaryResponse(lp, (long) lp.getLessons().size()));
        return pageResponseMapper.toPageResponse(pages);
    }
}
