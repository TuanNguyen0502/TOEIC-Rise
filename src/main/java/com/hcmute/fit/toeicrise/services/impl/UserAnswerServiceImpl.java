package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.responses.UserAnswerDetailResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.entities.UserAnswer;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.UserAnswerMapper;
import com.hcmute.fit.toeicrise.repositories.UserAnswerRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionGroupService;
import com.hcmute.fit.toeicrise.services.interfaces.IUserAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAnswerServiceImpl implements IUserAnswerService {
    private final IQuestionGroupService questionGroupService;
    private final UserAnswerRepository userAnswerRepository;
    private final UserAnswerMapper userAnswerMapper;

    @Override
    public UserAnswerDetailResponse getUserAnswerDetailResponse(Long userAnswerId) {
        UserAnswer userAnswer = userAnswerRepository.findById(userAnswerId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Answer"));
        QuestionGroup questionGroup = questionGroupService.getQuestionGroupEntity(userAnswer.getQuestionGroupId());
        return userAnswerMapper.toUserAnswerDetailResponse(userAnswer, questionGroup);
    }
}
