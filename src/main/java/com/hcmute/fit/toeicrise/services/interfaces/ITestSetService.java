package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.TestSetRequest;
import com.hcmute.fit.toeicrise.dtos.requests.UpdateTestSetRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestSetDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestSetResponse;
import com.hcmute.fit.toeicrise.models.enums.ETestSetStatus;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;

public interface ITestSetService {
    PageResponse getAllTestSets(String name,
                                ETestSetStatus status,
                                int page,
                                int size,
                                String sortBy,
                                String direction);

    TestSetDetailResponse getTestSetDetailById(Long testSetId,
                                               String name,
                                               ETestStatus status,
                                               int page,
                                               int size,
                                               String sortBy,
                                               String direction);

    void deleteTestSetById(Long id);
    void addTestSet(TestSetRequest testSetRequest);
    TestSetResponse updateTestSet(UpdateTestSetRequest updateTestSetRequest);
}
