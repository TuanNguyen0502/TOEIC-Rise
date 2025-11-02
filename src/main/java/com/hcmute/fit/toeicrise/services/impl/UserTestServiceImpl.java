package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.UserAnswerRequest;
import com.hcmute.fit.toeicrise.dtos.requests.UserTestRequest;
import com.hcmute.fit.toeicrise.dtos.responses.TestResultOverallResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestResultResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.*;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.UserTestMapper;
import com.hcmute.fit.toeicrise.repositories.TestRepository;
import com.hcmute.fit.toeicrise.repositories.UserRepository;
import com.hcmute.fit.toeicrise.repositories.UserTestRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionGroupService;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionService;
import com.hcmute.fit.toeicrise.services.interfaces.IUserTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserTestServiceImpl implements IUserTestService {
    private final IQuestionService questionService;
    private final IQuestionGroupService questionGroupService;
    private final TestRepository testRepository;
    private final UserRepository userRepository;
    private final UserTestRepository userTestRepository;
    private final UserTestMapper userTestMapper;

    @Override
    public TestResultResponse getUserTestResultById(String email, Long userTestId) {
        UserTest userTest = userTestRepository.findById(userTestId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "UserTest"));
        // Verify that the userTest belongs to the user with the given email
        if (!userTest.getUser().getAccount().getEmail().equals(email)) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test Result");
        }
        return userTestMapper.toTestResultResponse(userTest);
    }

    @Transactional
    @Override
    public TestResultOverallResponse calculateAndSaveUserTestResult(String email, UserTestRequest request) {
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

        if (request.getParts() == null || request.getParts().isEmpty()) {
            calculateExamScore(userTest, request.getAnswers());
        } else {
            calculatePracticeScore(userTest, request.getAnswers());
        }

        userTestRepository.save(userTest);
        return userTestMapper.toTestResultOverallResponse(userTest);
    }

    private void calculatePracticeScore(UserTest userTest, List<UserAnswerRequest> answers) {
        int correctAnswers = 0;

        for (UserAnswerRequest answerRequest : answers) {
            Question question = questionService.getQuestionEntityById(answerRequest.getQuestionId());
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
        userTest.setCorrectPercent(((double) correctAnswers / answers.size()) * 100);
    }

    private void calculateExamScore(UserTest userTest, List<UserAnswerRequest> answers) {
        int correctAnswers = 0;
        int listeningCorrect = 0;
        int readingCorrect = 0;

        // Group answers by questionGroupId
        Map<Long, List<UserAnswerRequest>> groupedByGroupId =
                answers.stream().collect(Collectors.groupingBy(UserAnswerRequest::getQuestionGroupId));

        // Process each group
        for (Map.Entry<Long, List<UserAnswerRequest>> entry : groupedByGroupId.entrySet()) {
            Long groupId = entry.getKey();
            List<UserAnswerRequest> groupAnswers = entry.getValue();

            // Fetch question group
            QuestionGroup questionGroup = questionGroupService.isListeningQuestionGroup(groupId);
            boolean isListeningPart = questionGroupService.isListeningPart(questionGroup.getPart());

            // Create a map of questionId to Question for quick lookup
            Map<Long, Question> questionMap = questionGroup.getQuestions().stream()
                    .collect(Collectors.toMap(Question::getId, q -> q));

            // Evaluate each answer in the group
            for (UserAnswerRequest answerRequest : groupAnswers) {
                // Fetch the corresponding question
                Question question = questionMap.get(answerRequest.getQuestionId());
                boolean isCorrect = answerRequest.getAnswer() != null && answerRequest.getAnswer().equals(question.getCorrectOption());
                if (isCorrect) {
                    correctAnswers++;
                    if (isListeningPart) listeningCorrect++;
                    else readingCorrect++;
                }

                userTest.getUserAnswers().add(UserAnswer.builder()
                        .userTest(userTest)
                        .question(question)
                        .questionGroupId(answerRequest.getQuestionGroupId())
                        .answer(answerRequest.getAnswer())
                        .isCorrect(isCorrect)
                        .build());
            }
        }

        userTest.setCorrectAnswers(correctAnswers);
        userTest.setCorrectPercent((double) correctAnswers / answers.size());
        userTest.setListeningCorrectAnswers(listeningCorrect);
        userTest.setReadingCorrectAnswers(readingCorrect);
    }
}
