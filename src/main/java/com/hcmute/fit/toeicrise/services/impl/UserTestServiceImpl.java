package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.UserAnswerRequest;
import com.hcmute.fit.toeicrise.dtos.requests.UserTestRequest;
import com.hcmute.fit.toeicrise.dtos.responses.TestResultOverallResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestResultResponse;
import com.hcmute.fit.toeicrise.dtos.responses.UserAnswerGroupedByTagResponse;
import com.hcmute.fit.toeicrise.dtos.responses.UserAnswerOverallResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.*;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.UserAnswerMapper;
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

import java.util.ArrayList;
import java.util.HashMap;
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
    private final UserAnswerMapper userAnswerMapper;

    @Override
    public TestResultResponse getUserTestResultById(String email, Long userTestId) {
        UserTest userTest = userTestRepository.findById(userTestId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "UserTest"));

        // Verify that the userTest belongs to the user with the given email
        if (!userTest.getUser().getAccount().getEmail().equals(email)) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test Result");
        }

        // Prepare data structure to hold grouped answers
        Map<String, List<UserAnswerGroupedByTagResponse>> userAnswersByPart = new HashMap<>(Map.of());
        List<UserAnswer> userAnswers = userTest.getUserAnswers();

        // Group user answers by part and tag
        Map<String, List<UserAnswer>> answersByPart = userAnswers.stream()
                .collect(Collectors.groupingBy(ua ->
                        questionGroupService.getPartNameByQuestionGroupId(ua.getQuestionGroupId())));

        // Process each part
        for (Map.Entry<String, List<UserAnswer>> entry : answersByPart.entrySet()) {
            String partName = entry.getKey();
            List<UserAnswer> answersInPart = entry.getValue();

            // Flatten all (tag, userAnswer) pairs and group by tag
            Map<String, List<UserAnswer>> answersByTag = answersInPart.stream()
                    .flatMap(ua -> ua.getQuestion().getTags().stream()
                            .map(tag -> Map.entry(tag.getName(), ua)))
                    .collect(Collectors.groupingBy(
                            Map.Entry::getKey,
                            Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                    ));

            // Prepare grouped responses for the part
            List<UserAnswerGroupedByTagResponse> groupedResponses = new ArrayList<>();

            // Process each tag
            for (Map.Entry<String, List<UserAnswer>> tagEntry : answersByTag.entrySet()) {
                String tag = tagEntry.getKey();
                List<UserAnswer> answersForTag = tagEntry.getValue();

                int correctAnswers = (int) answersForTag.stream().filter(UserAnswer::getIsCorrect).count();
                int wrongAnswers = answersForTag.size() - correctAnswers;
                double correctPercent = answersForTag.isEmpty() ? 0.0 : ((double) correctAnswers / answersForTag.size()) * 100;
                List<Long> userAnswerIds = answersForTag.stream().map(UserAnswer::getId).toList();

                groupedResponses.add(UserAnswerGroupedByTagResponse.builder()
                        .tag(tag)
                        .correctAnswers(correctAnswers)
                        .wrongAnswers(wrongAnswers)
                        .correctPercent(correctPercent)
                        .userAnswerIds(userAnswerIds)
                        .build());
            }

            // Add summary for the part
            int totalCorrect = (int) answersInPart.stream().filter(UserAnswer::getIsCorrect).count();
            int totalQuestions = answersInPart.size();
            int totalWrong = totalQuestions - totalCorrect;
            double totalPercent = totalQuestions == 0 ? 0.0 : ((double) totalCorrect / totalQuestions) * 100;

            groupedResponses.add(
                    UserAnswerGroupedByTagResponse.builder()
                            .tag("Total")
                            .correctAnswers(totalCorrect)
                            .wrongAnswers(totalWrong)
                            .correctPercent(totalPercent)
                            .userAnswerIds(null)
                            .build()
            );

            // Add to the final map
            userAnswersByPart.put(partName, groupedResponses);
        }

        return userTestMapper.toTestResultResponse(userTest, userAnswersByPart);
    }

    @Override
    public Map<String, List<UserAnswerOverallResponse>> getUserAnswersGroupedByPart(String email, Long userTestId) {
        UserTest userTest = userTestRepository.findById(userTestId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "UserTest"));

        // Verify that the userTest belongs to the user with the given email
        if (!userTest.getUser().getAccount().getEmail().equals(email)) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test Result");
        }

        Map<String, List<UserAnswerOverallResponse>> result = new HashMap<>();

        for (UserAnswer userAnswer : userTest.getUserAnswers()) {
            String partName = questionGroupService.getPartNameByQuestionGroupId(userAnswer.getQuestionGroupId());
            UserAnswerOverallResponse answerResponse = userAnswerMapper.toUserAnswerOverallResponse(userAnswer);

            result.computeIfAbsent(partName, _ -> new ArrayList<>()).add(answerResponse);
        }

        return result;
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
            QuestionGroup questionGroup = questionGroupService.getQuestionGroupWithQuestionsEntity(groupId);
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
