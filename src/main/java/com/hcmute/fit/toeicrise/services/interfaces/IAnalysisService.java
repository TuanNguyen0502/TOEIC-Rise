package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.models.enums.EDays;

public interface IAnalysisService {
    PageResponse getAllTestHistory(EDays days, int page, int size, String email);
}
