package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.TestSetRequest;
import com.hcmute.fit.toeicrise.dtos.requests.UpdateTestSetRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestSetDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestSetResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.TestSet;
import com.hcmute.fit.toeicrise.models.enums.ETestSetStatus;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.models.mappers.TestSetMapper;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TestSetServiceImpl implements ITestSetService {
    private final TestSetRepository testSetRepository;
    private final ITestService testService;
    private final TestSetMapper testSetMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    public PageResponse getAllTestSets(String name,
                                       ETestSetStatus status,
                                       int page,
                                       int size,
                                       String sortBy,
                                       String direction) {
        Specification<TestSet> specification = (_, _, cb) -> cb.conjunction();
        if (name != null && !name.isEmpty()) {
            specification = specification.and(TestSetSpecification.nameContains(name));
        }
        specification = specification.and(TestSetSpecification.statusEquals(Objects.requireNonNullElse(status, ETestSetStatus.IN_USE)));

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TestSetResponse> testSetResponses = testSetRepository.findAll(specification, pageable)
                .map(testSetMapper::toTestSetResponse);

        return pageResponseMapper.toPageResponse(testSetResponses);
    }

    @Override
    public List<TestSetResponse> getAllTestSet() {
        return testSetRepository.getAllByStatus().stream().map(testSetMapper::toTestSetResponse).toList();
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
        PageResponse testResponses = testService.getTestsByTestSetId(testSetId, name, status, page, size, sortBy, direction);
        // Map to detail response
        return testSetMapper.toTestSetDetailResponse(testSet, testResponses);
    }

    @Override
    public void deleteTestSetById(Long id) {
        TestSet testSet = testSetRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test set"));
        testSet.setStatus(ETestSetStatus.DELETED);
        testSetRepository.save(testSet);
        // Also mark all tests in this test set as DELETED
        testService.deleteTestsByTestSetId(id);
    }

    @Override
    @Transactional
    public void addTestSet(TestSetRequest testSetRequest) {
        if (testSetRepository.existsByName(testSetRequest.getTestName())) {
            throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Test set's name");
        }
        TestSet testSet = TestSet.builder()
                .name(testSetRequest.getTestName())
                .status(ETestSetStatus.IN_USE).build();
        testSetRepository.save(testSet);
    }

    @Override
    @Transactional
    public TestSetResponse updateTestSet(UpdateTestSetRequest updateTestSetRequest) {
        TestSet oldTestSet = testSetRepository.findById(updateTestSetRequest.getId()).orElse(null);
        if (oldTestSet == null) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test set");
        }
        TestSet testSet = testSetRepository.findByName(updateTestSetRequest.getTestName()).orElse(null);
        if (testSet != null && !testSet.getId().equals(updateTestSetRequest.getId())) {
            throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Test set's name");
        }
        oldTestSet.setName(updateTestSetRequest.getTestName());
        oldTestSet.setStatus(updateTestSetRequest.getStatus());
        testSetRepository.save(oldTestSet);
        return testSetMapper.toTestSetResponse(oldTestSet);
    }
}
