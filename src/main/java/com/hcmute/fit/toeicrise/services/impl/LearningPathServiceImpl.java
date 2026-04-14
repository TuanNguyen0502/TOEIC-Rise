package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.learningpath.*;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learningpath.LearningPathDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learningpath.LearningPathSummaryResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learningpath.LessonResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.LearningPath;
import com.hcmute.fit.toeicrise.models.entities.Lesson;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.LearningPathMapper;
import com.hcmute.fit.toeicrise.models.mappers.LessonMapper;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.repositories.LearningPathRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.LearningPathSpecification;
import com.hcmute.fit.toeicrise.services.interfaces.ILearningPathService;
import com.hcmute.fit.toeicrise.services.interfaces.ILessonService;
import com.hcmute.fit.toeicrise.services.interfaces.IUserService;
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
    private final ILessonService lessonService;
    private final IUserService userService;
    private final LearningPathMapper learningPathMapper;
    private final LessonMapper lessonMapper;
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
    public LearningPathDetailResponse getLearningPathDetail(Long learningPathId) {
        LearningPath path = learningPathRepository.findLearningPathWithLessonsById(learningPathId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Learning Path"));

        List<LessonResponse> lessonResponses = path.getLessons() == null ? List.of() : path.getLessons().stream()
                .sorted(Comparator.comparing(Lesson::getOrderIndex))
                .map(lessonMapper::toResponse)
                .toList();
        LearningPathDetailResponse response = learningPathMapper.toLearningPathDetailResponse(path);
        response.setLessons(lessonResponses);
        return response;
    }

    @Override
    public LearningPathDetailResponse getLearningPathDetailForAdmin(Long learningPathId) {
        return getLearningPathDetail(learningPathId);
    }

    @Override
    @Transactional
    public void createLearningPath(LearningPathCreateRequest request) {
        LearningPath path = learningPathMapper.toEntity(request);
        path.setIsActive(true);
        learningPathRepository.save(path);
    }

    @Transactional
    @Override
    public void updateLearningPath(Long learningPathId, LearningPathUpdateRequest request) {
        LearningPath path = learningPathRepository.findById(learningPathId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Learning Path"));
        path.setName(request.getName());
        path.setDescription(request.getDescription());
        path.setIsActive(request.getIsActive());
        learningPathRepository.save(path);
    }

    @Override
    public LearningPath getLearningPath(Long learningPathId) {
        return learningPathRepository.findLearningPathWithLessonsById(learningPathId).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Learning path"));
    }

    @Transactional
    @Override
    public LessonResponse createLesson(Long learningPathId, LessonCreateRequest request) {
        LearningPath path = getLearningPath(learningPathId);
        return lessonService.createLesson(request, path);
    }

    @Transactional
    @Override
    public void reorderLessons(Long learningPathId, LessonReorderRequest request) {
        LearningPath path = getLearningPath(learningPathId);
        lessonService.reorderLesson(request, path);
    }

    @Override
    public PageResponse listActiveLearningPaths(int page, int size, String sortBy, String direction) {
        return listLearningPaths(true, page, size, sortBy, direction);
    }

    @Override
    public LearningPathDetailResponse getLearningPathDetailForLearner(String email, Long learningPathId) {
        userService.getUserByEmail(email);
        return getLearningPathDetail(learningPathId);
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
