package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.responses.TestResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Test;
import com.hcmute.fit.toeicrise.models.enums.ETestSetStatus;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.repositories.TestRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.TestSpecification;
import com.hcmute.fit.toeicrise.services.interfaces.ITestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements ITestService {
    private final TestRepository testRepository;

    @Override
    public Page<TestResponse> getTestsByTestSetId(Long testSetId, String name, String status, int page, int size, String sortBy, String direction) {
        Specification<Test> specification = (_, _, cb) -> cb.conjunction();
        specification = specification.and(TestSpecification.testSetIdEquals(testSetId));
        if (name != null && !name.isEmpty()) {
            specification = specification.and(TestSpecification.nameContains(name));
        }
        if (status != null && !status.isEmpty()) {
            if (Arrays.stream(ETestStatus.values()).noneMatch(s -> s.name().equals(status))) {
                throw new AppException(ErrorCode.VALIDATION_ERROR, "status");
            }
            specification = specification.and(TestSpecification.statusEquals(status));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return testRepository.findAll(specification, pageable)
                .map(test -> TestResponse.builder()
                        .id(test.getId())
                        .name(test.getName())
                        .status(test.getStatus().name().replace("_", " "))
                        .createdAt(test.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .updatedAt(test.getUpdatedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .build());
    }
}
