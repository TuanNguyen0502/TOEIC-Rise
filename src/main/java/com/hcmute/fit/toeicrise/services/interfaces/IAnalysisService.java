package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;

public interface IAnalysisService {
    PageResponse getAllTestHistory(int page, int size, String email);
}
