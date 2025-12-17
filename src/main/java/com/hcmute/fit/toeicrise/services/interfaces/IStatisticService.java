package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.statistic.SystemOverviewResponse;

public interface IStatisticService {
    SystemOverviewResponse getSystemOverview();
}
