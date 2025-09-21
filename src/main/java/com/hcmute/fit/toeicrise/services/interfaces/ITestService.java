package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.TestUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.TestDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestResponse;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;

public interface ITestService {
    Page<TestResponse> getAllTests(String name, ETestStatus status, int page, int size, String sortBy, String direction);

    Page<TestResponse> getTestsByTestSetId(Long testSetId, String name, ETestStatus status, int page, int size, String sortBy, String direction);

    TestResponse updateTest(Long id, TestUpdateRequest testUpdateRequest);

    boolean deleteTestById(Long id);

    @Async
    void deleteTestsByTestSetId(Long testSetId);

    TestDetailResponse getTestDetailById(Long id, String part, int page, int size, String sortBy, String direction);
}
