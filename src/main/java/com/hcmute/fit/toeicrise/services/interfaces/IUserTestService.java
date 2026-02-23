package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.usertest.UserTestRequest;
import com.hcmute.fit.toeicrise.dtos.responses.analysis.AnalysisResultResponse;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.analysis.FullTestResultResponse;
import com.hcmute.fit.toeicrise.dtos.responses.statistic.ActivityTrendResponse;
import com.hcmute.fit.toeicrise.dtos.responses.statistic.ScoreDistInsightResponse;
import com.hcmute.fit.toeicrise.dtos.responses.statistic.TestModeInsightResponse;
import com.hcmute.fit.toeicrise.dtos.responses.usertest.TestResultOverallResponse;
import com.hcmute.fit.toeicrise.dtos.responses.usertest.TestResultResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestHistoryResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestPartsResponse;
import com.hcmute.fit.toeicrise.dtos.responses.useranswer.UserAnswerOverallResponse;
import com.hcmute.fit.toeicrise.models.enums.EDays;
import com.hcmute.fit.toeicrise.models.entities.UserTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IUserTestService {
    TestResultResponse getUserTestResultById(String email, Long userTestId);

    Map<String, List<UserAnswerOverallResponse>> getUserAnswersGroupedByPart(String email, Long userTestId);

    TestResultOverallResponse calculateAndSaveUserTestResult(String email, UserTestRequest request);

    List<LearnerTestHistoryResponse> allLearnerTestHistories(Long testId, String email);

    LearnerTestPartsResponse getTestByIdAndParts(Long testId, List<Long> parts);

    LearnerTestPartsResponse getUserTestDetail(Long userTestId, String email);

    AnalysisResultResponse getAnalysisResult(String email, EDays days);
  
    PageResponse getAllHistories(Specification<UserTest> userTestSpecification, Pageable pageable);

    FullTestResultResponse getFullTestResult(String email, int size);

    Long totalUserTest();

    ActivityTrendResponse getActivityTrend(LocalDateTime from, LocalDateTime to);

    TestModeInsightResponse getTestModeInsight(LocalDateTime start, LocalDateTime end);

    ScoreDistInsightResponse getScoreInsight(LocalDateTime start, LocalDateTime end);

    Long totalUserTest(LocalDateTime from, LocalDateTime to);
}
