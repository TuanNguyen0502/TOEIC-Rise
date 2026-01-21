package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.testset.TestSetRequest;
import com.hcmute.fit.toeicrise.dtos.requests.testset.UpdateTestSetRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.testset.TestSetDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.testset.TestSetResponse;
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
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class TestSetServiceImpl implements ITestSetService {
    private final TestSetRepository testSetRepository;
    private final ITestService testService;
    private final TestSetMapper testSetMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse getAllTestSets(String name, ETestSetStatus status, int page, int size, String sortBy, String direction) {
        Specification<TestSet> specification = (_, _, cb) -> cb.conjunction();
        if (name != null && !name.isEmpty())
            specification = specification.and(TestSetSpecification.nameContains(name));
        if (status == null)
            status = ETestSetStatus.IN_USE;
        specification = specification.and(TestSetSpecification.statusEquals(status));

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TestSetResponse> testSetResponses = testSetRepository.findAll(specification, pageable)
                .map(testSetMapper::toTestSetResponse);

        return pageResponseMapper.toPageResponse(testSetResponses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestSetResponse> getAllTestSets() {
        return testSetRepository.getAllByStatus(ETestSetStatus.IN_USE).stream().map(testSetMapper::toTestSetResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TestSetDetailResponse getTestSetDetailById(Long testSetId, String name, ETestStatus status, int page, int size,
                                                      String sortBy, String direction) {
        TestSet testSet = findTestSetById(testSetId);
        PageResponse testResponses = testService.getTestsByTestSetId(testSetId, name, status, page, size, sortBy, direction);
        return testSetMapper.toTestSetDetailResponse(testSet, testResponses);
    }

    @Override
    @Transactional
    public void deleteTestSetById(Long id) {
        TestSet testSet = findTestSetById(id);
        testSet.setStatus(ETestSetStatus.DELETED);
        testSetRepository.save(testSet);
        log.info("Test set deleted successfully with id {}", id);
        testService.deleteTestsByTestSetId(id);
    }

    @Override
    @Transactional
    public void addTestSet(TestSetRequest testSetRequest) {
        if (testSetRepository.existsByName(testSetRequest.getTestName()))
            throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Test set's name");
        TestSet testSet = TestSet.builder()
                .name(testSetRequest.getTestName())
                .status(ETestSetStatus.IN_USE).build();
        testSetRepository.save(testSet);
        log.info("Test set added successfully with name {}", testSetRequest.getTestName());
    }

    @Override
    @Transactional
    public TestSetResponse updateTestSet(UpdateTestSetRequest updateTestSetRequest) {
        TestSet oldTestSet = findTestSetById(updateTestSetRequest.getId());
        testSetRepository.findByName(updateTestSetRequest.getTestName()).ifPresent(
                existingTestSet -> {
                    if (!existingTestSet.getId().equals(updateTestSetRequest.getId())){
                        throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Test set's name");
                    }
                }
        );
        oldTestSet.setName(updateTestSetRequest.getTestName());

        if (updateTestSetRequest.getStatus() != null && !oldTestSet.getStatus().equals(updateTestSetRequest.getStatus())) {
            handleChangeTestSetStatus(updateTestSetRequest.getStatus(), updateTestSetRequest.getId());
            oldTestSet.setStatus(updateTestSetRequest.getStatus());
        }

        testSetRepository.save(oldTestSet);
        log.info("Test set updated successfully with id {}", updateTestSetRequest.getId());
        return testSetMapper.toTestSetResponse(oldTestSet);
    }

    @Override
    public Long totalTestSets() {
        return testSetRepository.count();
    }

    @Override
    public TestSet findTestSetById(Long testSetId) {
        return testSetRepository.findById(testSetId).orElseThrow(
                () -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test set")
        );
    }

    private void handleChangeTestSetStatus(ETestSetStatus status, Long testSetId) {
        if (status == ETestSetStatus.DELETED)
            testService.deleteTestsByTestSetId(testSetId);
        if (status == ETestSetStatus.IN_USE)
            testService.changeTestsStatusToPendingByTestSetId(testSetId);
    }
}
