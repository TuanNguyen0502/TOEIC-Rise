package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.TestSetRequest;
import com.hcmute.fit.toeicrise.dtos.requests.UpdateTestSetRequest;
import com.hcmute.fit.toeicrise.dtos.responses.TestSetDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestSetResponse;
import com.hcmute.fit.toeicrise.models.entities.TestSet;
import org.springframework.data.domain.Page;

public interface ITestSetService {
    Page<TestSetResponse> getAllTestSets(String name,
                                         String status,
                                         int page,
                                         int size,
                                         String sortBy,
                                         String direction);

    TestSetDetailResponse getTestSetDetailById(Long testSetId,
                                               String name,
                                               String status,
                                               int page,
                                               int size,
                                               String sortBy,
                                               String direction);

    void deleteTestSetById(Long id);
    void addTestSet(TestSetRequest testSetRequest);
    TestSet updateTestSet(UpdateTestSetRequest updateTestSetRequest);
}
