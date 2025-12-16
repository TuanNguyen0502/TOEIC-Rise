package com.hcmute.fit.toeicrise.dtos.responses.statistic;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemOverviewResponse {
    private long totalAccounts;
    private long totalLearners;
    private long totalStaffs;

    private long totalTestSets;
    private long totalTests;
    private long totalFlashcards;

    private long totalSubmissions;
    private long totalConversations;
    private long totalReports;
}
