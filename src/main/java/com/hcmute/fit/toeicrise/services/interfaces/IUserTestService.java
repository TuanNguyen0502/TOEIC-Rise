package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.usertest.UserTestRequest;
import com.hcmute.fit.toeicrise.dtos.responses.analysis.AnalysisResultResponse;
import com.hcmute.fit.toeicrise.dtos.responses.usertest.TestResultOverallResponse;
import com.hcmute.fit.toeicrise.dtos.responses.usertest.TestResultResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestHistoryResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestPartsResponse;
import com.hcmute.fit.toeicrise.dtos.responses.useranswer.UserAnswerOverallResponse;
import com.hcmute.fit.toeicrise.models.enums.EDays;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface IUserTestService {
    TestResultResponse getUserTestResultById(String email, Long userTestId);

    Map<String, List<UserAnswerOverallResponse>> getUserAnswersGroupedByPart(String email, Long userTestId);

    @Transactional
    TestResultOverallResponse calculateAndSaveUserTestResult(String email, UserTestRequest request);

    List<LearnerTestHistoryResponse> allLearnerTestHistories(Long testId, String email);

    LearnerTestPartsResponse getTestByIdAndParts(Long testId, List<Long> parts);

    LearnerTestPartsResponse getUserTestDetail(Long userTestId, String email);

    AnalysisResultResponse getAnalysisResult(String email, EDays days);
}
