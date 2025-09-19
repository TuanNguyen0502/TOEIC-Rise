package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.responses.TestSetDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestSetResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Test;
import com.hcmute.fit.toeicrise.models.entities.TestSet;
import com.hcmute.fit.toeicrise.models.enums.ETestSetStatus;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.repositories.TestRepository;
import com.hcmute.fit.toeicrise.repositories.TestSetRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.TestSetSpecification;
import com.hcmute.fit.toeicrise.repositories.specifications.TestSpecification;
import com.hcmute.fit.toeicrise.services.interfaces.ITestSetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class TestSetServiceImpl implements ITestSetService {
    private final TestSetRepository testSetRepository;
    private final TestRepository testRepository;

    @Override
    public Page<TestSetResponse> getAllTestSets(String name,
                                                String status,
                                                int page,
                                                int size,
                                                String sortBy,
                                                String direction) {
        Specification<TestSet> specification = (_, _, cb) -> cb.conjunction();
        if (name != null && !name.isEmpty()) {
            specification = specification.and(TestSetSpecification.nameContains(name));
        }
        if (status != null && !status.isEmpty()) {
            if (Arrays.stream(ETestSetStatus.values()).noneMatch(s -> s.name().equals(status))) {
                throw new AppException(ErrorCode.VALIDATION_ERROR, "status");
            }
            specification = specification.and(TestSetSpecification.statusEquals(status));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return testSetRepository.findAll(specification, pageable)
                .map(testSet -> TestSetResponse.builder()
                        .id(testSet.getId())
                        .name(testSet.getName())
                        .status(testSet.getStatus().name().replace("_", " "))
                        .createdAt(testSet.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .updatedAt(testSet.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .build());
    }

    @Override
    public TestSetDetailResponse getTestSetDetailById(Long testSetId,
                                                      String name,
                                                      String status,
                                                      int page,
                                                      int size,
                                                      String sortBy,
                                                      String direction) {
        // Check if test set exists
        TestSet testSet = testSetRepository.findById(testSetId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test set"));

        // Get tests in the test set with filtering and pagination
        Specification<Test> specification = (_, _, cb) -> cb.conjunction();
        specification = specification.and(TestSpecification.testSetIdEquals(testSetId));
        if (name != null && !name.isEmpty()) {
            specification = specification.and(TestSpecification.nameContains(name));
        }
        if (status != null && !status.isEmpty()) {
            if (Arrays.stream(ETestSetStatus.values()).noneMatch(s -> s.name().equals(status))) {
                throw new AppException(ErrorCode.VALIDATION_ERROR, "status");
            }
            specification = specification.and(TestSpecification.statusEquals(status));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Test> tests = testRepository.findAll(specification, pageable);

        return TestSetDetailResponse.builder()
                .id(testSet.getId())
                .name(testSet.getName())
                .status(testSet.getStatus().name().replace("_", " "))
                .createdAt(testSet.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .updatedAt(testSet.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .testSetResponses(tests.map(test -> TestSetResponse.builder()
                        .id(test.getId())
                        .name(test.getName())
                        .status(test.getStatus().name().replace("_", " "))
                        .createdAt(test.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .updatedAt(test.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .build()))
                .build();
    }

    @Override
    public void deleteTestSetById(Long id) {
        TestSet testSet = testSetRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test set"));
        testSet.setStatus(ETestSetStatus.DELETED);
        testSetRepository.save(testSet);
    }
}
