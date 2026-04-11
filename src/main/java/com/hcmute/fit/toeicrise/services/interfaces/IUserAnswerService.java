package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.useranswer.UserAnswerDetailResponse;
import com.hcmute.fit.toeicrise.models.entities.UserAnswer;

import java.util.List;

public interface IUserAnswerService {
    UserAnswerDetailResponse getUserAnswerDetailResponse(Long userAnswerId);

    List<UserAnswer> getUserTestIdInWithQuestion(List<Long> userTestIds);

    String getOrGenerateWritingFeedback(Long userAnswerId);
}
