package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.responses.statistic.SystemOverviewResponse;
import com.hcmute.fit.toeicrise.models.enums.ERole;
import com.hcmute.fit.toeicrise.repositories.ChatMemoryRepository;
import com.hcmute.fit.toeicrise.services.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
                .totalLearners(authenticationService.countAllUsersWithRole(ERole.LEARNER))
                .totalStaffs(authenticationService.countAllUsersWithRole(ERole.STAFF) + authenticationService.countAllUsersWithRole(ERole.ADMIN))
                .totalTestSets(testSetService.totalTestSets())
                .totalTests(testService.totalTest())
                .totalFlashcards(flashcardService.totalFlashcards())
                .totalSubmissions(userTestService.totalUserTest())
                .totalConversations(chatMemoryRepository.countAllConversation())
                .totalReports(questionReportService.totalReports())
                .build();
    }
}
