package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.testset.TestSetRequest;
import com.hcmute.fit.toeicrise.dtos.requests.testset.UpdateTestSetRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.testset.TestSetDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.testset.TestSetResponse;
import com.hcmute.fit.toeicrise.models.entities.TestSet;
import com.hcmute.fit.toeicrise.models.enums.ETestSetStatus;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ITestSetService {
    PageResponse getAllListeningReadingTestSets(String name, ETestSetStatus status, int page, int size, String sortBy, String direction);

    @Transactional(readOnly = true)
    PageResponse getAllSpeakingTestSets(String name, ETestSetStatus status, int page, int size, String sortBy, String direction);

    @Transactional(readOnly = true)
    PageResponse getAllWritingTestSets(String name, ETestSetStatus status, int page, int size, String sortBy, String direction);

    List<TestSetResponse> getAllTestSets();

    TestSetDetailResponse getTestSetDetailById(Long testSetId, String name, ETestStatus status, int page, int size,
                                               String sortBy, String direction);

    void deleteTestSetById(Long id);

    void addTestSet(TestSetRequest testSetRequest);

    TestSetResponse updateTestSet(UpdateTestSetRequest updateTestSetRequest);

    Long totalTestSets();

    TestSet findTestSetById(Long testSetId);
}
