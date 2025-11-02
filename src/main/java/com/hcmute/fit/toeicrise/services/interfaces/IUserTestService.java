package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.UserTestRequest;
import com.hcmute.fit.toeicrise.dtos.responses.TestResultOverallResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestResultResponse;
import com.hcmute.fit.toeicrise.dtos.responses.UserAnswerOverallResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface IUserTestService {
    TestResultResponse getUserTestResultById(String email, Long userTestId);

    Map<String, List<UserAnswerOverallResponse>> getUserAnswersGroupedByPart(String email, Long userTestId);

    @Transactional
    TestResultOverallResponse calculateAndSaveUserTestResult(String email, UserTestRequest request);
}
