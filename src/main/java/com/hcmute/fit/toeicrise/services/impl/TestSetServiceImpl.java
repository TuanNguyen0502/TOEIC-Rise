package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.dtos.responses.TestResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestSetDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestSetResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.TestSet;
import com.hcmute.fit.toeicrise.models.enums.ETestSetStatus;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.repositories.TestSetRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.TestSetSpecification;
import com.hcmute.fit.toeicrise.services.interfaces.ITestService;
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
    private final ITestService testService;

    @Override
    public Page<TestSetResponse> getAllTestSets(String name,
                                                ETestSetStatus status,
                                                int page,
                                                int size,
                                                String sortBy,
                                                String direction) {
        Specification<TestSet> specification = (_, _, cb) -> cb.conjunction();
        if (name != null && !name.isEmpty()) {
            specification = specification.and(TestSetSpecification.nameContains(name));
        }
        if (status != null) {
            specification = specification.and(TestSetSpecification.statusEquals(status));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return testSetRepository.findAll(specification, pageable)
                .map(testSet -> TestSetResponse.builder()
                        .id(testSet.getId())
                        .name(testSet.getName())
                        .status(testSet.getStatus().name().replace("_", " "))
                        .createdAt(testSet.getCreatedAt().format(DateTimeFormatter.ofPattern(Constant.DATE_TIME_PATTERN)))
                        .updatedAt(testSet.getUpdatedAt().format(DateTimeFormatter.ofPattern(Constant.DATE_TIME_PATTERN)))
                        .build());
    }

    @Override
    public TestSetDetailResponse getTestSetDetailById(Long testSetId,
                                                      String name,
                                                      ETestStatus status,
                                                      int page,
                                                      int size,
                                                      String sortBy,
                                                      String direction) {
        // Check if test set exists
        TestSet testSet = testSetRepository.findById(testSetId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test set"));

        // Get tests in the test set with filtering and pagination
        Page<TestResponse> testResponses = testService.getTestsByTestSetId(testSetId, name, status, page, size, sortBy, direction);

        return TestSetDetailResponse.builder()
                .id(testSet.getId())
                .name(testSet.getName())
                .status(testSet.getStatus().name().replace("_", " "))
                .createdAt(testSet.getCreatedAt().format(DateTimeFormatter.ofPattern(Constant.DATE_TIME_PATTERN)))
                .updatedAt(testSet.getUpdatedAt().format(DateTimeFormatter.ofPattern(Constant.DATE_TIME_PATTERN)))
                .testResponses(testResponses)
                .build();
    }

    @Override
    public void deleteTestSetById(Long id) {
        TestSet testSet = testSetRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test set"));
        testSet.setStatus(ETestSetStatus.DELETED);
        testSetRepository.save(testSet);
    }
}
