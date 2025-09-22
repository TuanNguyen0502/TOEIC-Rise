package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.TestResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ITestService {
    Page<TestResponse> getTestsByTestSetId(Long testSetId, String name, String status, int page, int size, String sortBy, String direction);

    void importTest(MultipartFile file, String testName, Long testSetId);
}
