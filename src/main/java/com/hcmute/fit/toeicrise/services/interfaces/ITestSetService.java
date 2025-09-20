package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.TestSetDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestSetResponse;
import com.hcmute.fit.toeicrise.models.enums.ETestSetStatus;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import org.springframework.data.domain.Page;

public interface ITestSetService {
    Page<TestSetResponse> getAllTestSets(String name,
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
}
