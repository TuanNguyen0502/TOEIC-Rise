package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.UserAnswerDetailResponse;

public interface IUserAnswerService {
    UserAnswerDetailResponse getUserAnswerDetailResponse(Long userAnswerId);
}
