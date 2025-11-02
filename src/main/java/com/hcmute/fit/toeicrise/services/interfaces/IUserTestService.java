package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.UserTestRequest;
import com.hcmute.fit.toeicrise.dtos.responses.TestResultResponse;
import org.springframework.transaction.annotation.Transactional;

public interface IUserTestService {
    @Transactional
    TestResultResponse calculateAndSaveUserTestResult(String email, UserTestRequest request);
}
