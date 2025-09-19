package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.TestSetResponse;
import org.springframework.data.domain.Page;

public interface ITestSetService {
    Page<TestSetResponse> getAllTestSets(String name,
                                         String status,
                                         int page,
                                         int size,
                                         String sortBy,
                                         String direction);

    void deleteTestSetById(Long id);
}
