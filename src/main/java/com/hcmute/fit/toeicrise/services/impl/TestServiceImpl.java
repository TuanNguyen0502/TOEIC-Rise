package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.TestUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.QuestionGroupResponse;
import com.hcmute.fit.toeicrise.dtos.responses.QuestionResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Test;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.TestMapper;
import com.hcmute.fit.toeicrise.repositories.TestRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.TestSpecification;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionGroupService;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionService;
import com.hcmute.fit.toeicrise.services.interfaces.ITestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements ITestService {
    private final TestRepository testRepository;
    private final IQuestionGroupService questionGroupService;
    private final IQuestionService questionService;
    private final TestMapper testMapper;

    @Override
    public Page<TestResponse> getAllTests(String name, ETestStatus status, int page, int size, String sortBy, String direction) {
        Specification<Test> specification = (_, _, cb) -> cb.conjunction();
        return getTestResponses(name, status, page, size, sortBy, direction, specification);
    }

    @Override
    public Page<TestResponse> getTestsByTestSetId(Long testSetId, String name, ETestStatus status, int page, int size, String sortBy, String direction) {
        Specification<Test> specification = (_, _, cb) -> cb.conjunction();
        specification = specification.and(TestSpecification.testSetIdEquals(testSetId));
        return getTestResponses(name, status, page, size, sortBy, direction, specification);
    }

    @Override
    public TestResponse updateTest(Long id, TestUpdateRequest testUpdateRequest) {
        // Validate test ID
        Test existingTest = testRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test"));

        // Check for name uniqueness
        Test testWithSameName = testRepository.findByName(testUpdateRequest.getName()).orElse(null);
        if (testWithSameName != null && !testWithSameName.getId().equals(existingTest.getId())) {
            throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Test's name");
        }

        // Update fields
        existingTest.setName(testUpdateRequest.getName());
        existingTest.setStatus(testUpdateRequest.getStatus());
        Test updatedTest = testRepository.save(existingTest);
        return testMapper.toResponse(updatedTest);
    }

    @Override
    public boolean deleteTestById(Long id) {
        Test test = testRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test"));
        test.setStatus(ETestStatus.DELETED);
        testRepository.save(test);
        return true;
    }

    @Async
    @Override
    public void deleteTestsByTestSetId(Long testSetId) {
        List<Test> tests = testRepository.findAllByTestSet_Id(testSetId);
        for (Test test : tests) {
            test.setStatus(ETestStatus.DELETED);
        }
        testRepository.saveAll(tests);
    }

    @Override
    public TestDetailResponse getTestDetailById(Long id, String part, int page, int size, String sortBy, String direction) {
        // Validate test ID
        Test test = testRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test"));

        // Fetch question groups and questions
        List<QuestionGroupResponse> questionGroups = questionGroupService.getQuestionGroupsByTestId(id);
        Page<QuestionResponse> questions = questionService.getQuestionsByTestId(id, part, page, size, sortBy, direction);
        return testMapper.toDetailResponse(test, questionGroups, questions);
    }

    private Page<TestResponse> getTestResponses(String name, ETestStatus status, int page, int size, String sortBy, String direction, Specification<Test> specification) {
        if (name != null && !name.isEmpty()) {
            specification = specification.and(TestSpecification.nameContains(name));
        }
        if (status != null) {
            specification = specification.and(TestSpecification.statusEquals(status));
        } else {
            // If status is null, get all statuses except DELETED
            specification = specification.and(TestSpecification.statusNotEquals(ETestStatus.DELETED));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return testRepository.findAll(specification, pageable).map(testMapper::toResponse);
    }
}
