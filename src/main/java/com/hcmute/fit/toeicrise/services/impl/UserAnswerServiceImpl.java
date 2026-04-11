package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.utils.ImageUtils;
import com.hcmute.fit.toeicrise.dtos.responses.ImageResource;
import com.hcmute.fit.toeicrise.dtos.responses.useranswer.UserAnswerDetailResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.entities.UserAnswer;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.UserAnswerMapper;
import com.hcmute.fit.toeicrise.repositories.UserAnswerRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IChatService;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionGroupService;
import com.hcmute.fit.toeicrise.services.interfaces.IUserAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAnswerServiceImpl implements IUserAnswerService {
    private final IQuestionGroupService questionGroupService;
    private final IChatService chatService;
    private final UserAnswerRepository userAnswerRepository;
    private final UserAnswerMapper userAnswerMapper;

    @Override
    public UserAnswerDetailResponse getUserAnswerDetailResponse(Long userAnswerId) {
        UserAnswer userAnswer = userAnswerRepository.findById(userAnswerId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Answer"));
        QuestionGroup questionGroup = questionGroupService.getQuestionGroupEntity(userAnswer.getQuestionGroupId());
        return userAnswerMapper.toUserAnswerDetailResponse(userAnswer, questionGroup);
    }

    @Override
    public List<UserAnswer> getUserTestIdInWithQuestion(List<Long> userTestIds) {
        return userAnswerRepository.findByUserTestIdInWithQuestion(userTestIds);
    }

    @Override
    public String getOrGenerateWritingFeedback(Long userAnswerId) {
        UserAnswer userAnswer = userAnswerRepository.findById(userAnswerId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Answer"));
        if (userAnswer.getFeedback() != null && !userAnswer.getFeedback().isBlank()) {
            return userAnswer.getFeedback();
        }

        String feedback;
        QuestionGroup questionGroup = questionGroupService.getQuestionGroupEntity(userAnswer.getQuestionGroupId());
        if (questionGroup.getImageUrl() != null && !questionGroup.getImageUrl().isBlank()) {
            try {
                ImageResource resource = ImageUtils.fetchImage(questionGroup.getImageUrl());

                try (InputStream is = resource.inputStream()) {
                    feedback = chatService.generateFeedbackForWritingTestAnswerWithImage(
                            userAnswer.getAnswerText(),
                            questionGroup.getPart().getName(),
                            questionGroup.getPassage(),
                            is,
                            resource.contentType()
                    );
                }
            } catch (IOException e) {
                throw new AppException(ErrorCode.INVALID_REQUEST, "Failed to fetch question image");
            }
        } else {
            feedback = chatService.generateFeedbackForWritingTestAnswerWithoutImage(
                    userAnswer.getAnswerText(),
                    questionGroup.getPart().getName(),
                    questionGroup.getPassage()
            );
        }
        userAnswer.setFeedback(feedback);
        userAnswerRepository.save(userAnswer);
        return feedback;
    }
}
