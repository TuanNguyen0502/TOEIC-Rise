package com.hcmute.fit.toeicrise.dtos.responses.statistic;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardResponse {
    private KpiResponse newLearners;
    private KpiResponse activeUsers;
    private KpiResponse totalTests;
    private KpiResponse aiConversations;
    private ActivityTrendResponse activityTrend;
    private DeepInsightsResponse deepInsights;
}
