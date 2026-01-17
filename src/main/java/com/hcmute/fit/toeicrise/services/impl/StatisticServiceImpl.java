package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.utils.DateRangeUtil;
import com.hcmute.fit.toeicrise.dtos.responses.DateRange;
import com.hcmute.fit.toeicrise.dtos.responses.statistic.*;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.enums.ERole;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.repositories.ChatMemoryRepository;
import com.hcmute.fit.toeicrise.services.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements IStatisticService {
    private final IAuthenticationService authenticationService;
    private final IUserService userService;
    private final ITestSetService testSetService;
    private final ITestService testService;
    private final IFlashcardService flashcardService;
    private final IUserTestService userTestService;
    private final ChatMemoryRepository chatMemoryRepository;
    private final IQuestionReportService questionReportService;

    @Cacheable(value = "systemOverview", key = "'global'")
    public SystemOverviewResponse getSystemOverview() {
        return SystemOverviewResponse.builder()
                .totalAccounts(userService.countAllUsers())
                .totalLearners(userService.countAllUsersWithRole(ERole.LEARNER))
                .totalStaffs(userService.countAllUsersWithRole(ERole.STAFF) + userService.countAllUsersWithRole(ERole.ADMIN))
                .totalTestSets(testSetService.totalTestSets())
                .totalTests(testService.totalTest())
                .totalFlashcards(flashcardService.totalFlashcards())
                .totalSubmissions(userTestService.totalUserTest())
                .totalConversations(chatMemoryRepository.countAllConversation())
                .totalReports(questionReportService.totalReports())
                .build();
    }

    @Override
    public AdminDashboardResponse getPerformanceAnalysis(LocalDate startDate, LocalDate endDate) {
        validateLocalDate(startDate, endDate);
        DateRange prevTime = DateRangeUtil.previousPeriod(startDate, endDate);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        KpiResponse newLearners = getNewLearners(start, end, prevTime);
        KpiResponse activeUsers = getActiveLearners(start, end, prevTime);
        KpiResponse aiConversations = getAiConversation(start, end, prevTime);
        KpiResponse totalTests = getTotalTest(start, end, prevTime);

        ActivityTrendResponse activityTrend = userTestService.getActivityTrend(start, end);
        DeepInsightsResponse deepInsightsResponse = DeepInsightsResponse.builder()
                .regSource(authenticationService.getRegSourceInsight(start, end))
                .scoreDist(userTestService.getScoreInsight(start, end))
                .testMode(userTestService.getTestModeInsight(start, end)).build();
        return AdminDashboardResponse.builder()
                .newLearners(newLearners)
                .activeUsers(activeUsers)
                .totalTests(totalTests)
                .aiConversations(aiConversations)
                .activityTrend(activityTrend)
                .deepInsights(deepInsightsResponse)
                .build();
    }

    void validateLocalDate(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) throw new AppException(ErrorCode.VALIDATION_ERROR);
        if (startDate.isAfter(endDate)) throw new AppException(ErrorCode.VALIDATION_ERROR);
        if (endDate.isAfter(LocalDate.now())) throw new AppException(ErrorCode.VALIDATION_ERROR);
    }

    private double calculatorGrowth(Long current, Long previous) {
        if (previous == 0)
            return current > 0 ? 100 : 0.0;
        return ((double) (current - previous) / previous) * 100;
    }

    private KpiResponse getAiConversation(LocalDateTime startDate, LocalDateTime endDate, DateRange prevTime) {
        Long current = chatMemoryRepository.countTotalAiConversation(startDate, endDate);
        Long prev = chatMemoryRepository.countTotalAiConversation(prevTime.getStart().atStartOfDay(), prevTime.getEnd().plusDays(1).atStartOfDay());

        return KpiResponse.builder().value(current)
                .growthPercentage(calculatorGrowth(current, prev)).build();
    }

    private KpiResponse getTotalTest(LocalDateTime startDate, LocalDateTime endDate, DateRange prevTime) {
        Long prev = userTestService.totalUserTest(prevTime.getStart().atStartOfDay(), prevTime.getEnd().plusDays(1).atStartOfDay());
        Long current = userTestService.totalUserTest(startDate, endDate);

        return KpiResponse.builder().value(current)
                .growthPercentage(calculatorGrowth(current, prev)).build();
    }

    private KpiResponse getActiveLearners(LocalDateTime startDate, LocalDateTime endDate, DateRange prevTime) {
        Long current = authenticationService.countActiveUser(startDate, endDate);
        Long prev = authenticationService.countActiveUser(prevTime.getStart().atStartOfDay(), prevTime.getEnd().plusDays(1).atStartOfDay());

        return KpiResponse.builder().value(current)
                .growthPercentage(calculatorGrowth(current, prev)).build();
    }

    private KpiResponse getNewLearners(LocalDateTime startDate, LocalDateTime endDate, DateRange prevTime) {
        Long current = userService.countUsersBetweenDays(startDate, endDate);
        Long prev = userService.countUsersBetweenDays(prevTime.getStart().atStartOfDay(), prevTime.getEnd().plusDays(1).atStartOfDay());

        return KpiResponse.builder().value(current)
                .growthPercentage(calculatorGrowth(current, prev)).build();
    }
}
