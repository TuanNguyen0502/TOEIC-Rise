package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.UserAnswerRequest;
import com.hcmute.fit.toeicrise.dtos.requests.UserTestRequest;
import com.hcmute.fit.toeicrise.dtos.responses.TestResultResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.*;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.UserTestMapper;
import com.hcmute.fit.toeicrise.repositories.TestRepository;
import com.hcmute.fit.toeicrise.repositories.UserRepository;
import com.hcmute.fit.toeicrise.repositories.UserTestRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionService;
import com.hcmute.fit.toeicrise.services.interfaces.IUserTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserTestServiceImpl implements IUserTestService {
    private final IQuestionService questionService;
    private final TestRepository testRepository;
    private final UserRepository userRepository;
    private final UserTestRepository userTestRepository;
    private final UserTestMapper userTestMapper;

    @Transactional
    @Override
    public TestResultResponse calculateAndSaveUserTestResult(String email, UserTestRequest request) {
        User user = userRepository.findByAccount_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User"));
        Test test = testRepository.findById(request.getTestId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test"));

        UserTest userTest = UserTest.builder()
                .user(user)
                .test(test)
                .totalQuestions(request.getAnswers().size())
                .timeSpent(request.getTimeSpent())
                .parts(request.getParts())
                .build();

        int correctAnswers = 0;

        for (UserAnswerRequest answerRequest : request.getAnswers()) {
            Question question = questionService.getCorrectOptionByQuestionId(answerRequest.getQuestionId());
            boolean isCorrect = answerRequest.getAnswer() != null && answerRequest.getAnswer().equals(question.getCorrectOption());
            if (isCorrect) correctAnswers++;

            userTest.getUserAnswers().add(UserAnswer.builder()
                    .userTest(userTest)
                    .question(question)
                    .questionGroupId(answerRequest.getQuestionGroupId())
                    .answer(answerRequest.getAnswer())
                    .isCorrect(isCorrect)
                    .build());
        }

        userTest.setCorrectAnswers(correctAnswers);
        userTest.setCorrectPercent((double) correctAnswers / request.getAnswers().size());
        userTestRepository.save(userTest);

        return userTestMapper.toTestResultResponse(userTest);
    }
}
