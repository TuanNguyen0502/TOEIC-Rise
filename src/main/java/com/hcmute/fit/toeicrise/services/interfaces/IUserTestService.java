package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.UserTestRequest;
import com.hcmute.fit.toeicrise.dtos.responses.TestResultOverallResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestResultResponse;
import org.springframework.transaction.annotation.Transactional;

public interface IUserTestService {
    TestResultResponse getUserTestResultById(String email, Long userTestId);

    @Transactional
    TestResultOverallResponse calculateAndSaveUserTestResult(String email, UserTestRequest request);
}
