package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.statistic.AdminDashboardResponse;
import com.hcmute.fit.toeicrise.dtos.responses.statistic.SystemOverviewResponse;

import java.time.LocalDate;

public interface IStatisticService {
    SystemOverviewResponse getSystemOverview();
    AdminDashboardResponse getPerformanceAnalysis(LocalDate startDate, LocalDate endDate);
}
