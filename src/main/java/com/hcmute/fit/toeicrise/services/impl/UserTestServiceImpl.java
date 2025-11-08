package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.UserAnswerRequest;
import com.hcmute.fit.toeicrise.dtos.requests.UserTestRequest;
import com.hcmute.fit.toeicrise.dtos.responses.TestResultOverallResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestResultResponse;
import com.hcmute.fit.toeicrise.dtos.responses.UserAnswerGroupedByTagResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestHistoryResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestPartResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestPartsResponse;
import com.hcmute.fit.toeicrise.dtos.responses.UserAnswerOverallResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.*;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.TestMapper;
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

import java.util.*;
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
    private final TestMapper testMapper;
    private final UserAnswerMapper userAnswerMapper;

    @Override
    public TestResultResponse getUserTestResultById(String email, Long userTestId) {
        UserTest userTest = userTestRepository.findByIdWithAnswersAndQuestions(userTestId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "UserTest"));

        // Get all tags involved in the user answers at once for better performance
        int totalTags = userTest.getUserAnswers().stream()
                .flatMap(ua -> ua.getQuestion().getTags().stream())
                .map(Tag::getName)
                .collect(Collectors.toSet())
                .size();

        // Verify that the userTest belongs to the user with the given email
        if (!userTest.getUser().getAccount().getEmail().equals(email)) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test Result");
        }

        // Prepare data structure to hold grouped answers
        Map<String, List<UserAnswerGroupedByTagResponse>> userAnswersByPart = new HashMap<>(Map.of());
        List<UserAnswer> userAnswers = userTest.getUserAnswers();

        // Get unique question group IDs from user answers
        Set<Long> questionGroupIds = userAnswers.stream()
                .map(UserAnswer::getQuestionGroupId)
                .collect(Collectors.toSet());

        // Get part names by question group IDs
        Map<Long, String> partNamesByGroupId = questionGroupService.getPartNamesByQuestionGroupIds(questionGroupIds);

        // Group user answers by part and tag
        Map<String, List<UserAnswer>> answersByPart = userAnswers.stream()
                .collect(Collectors.groupingBy(ua ->
                        partNamesByGroupId.get(ua.getQuestionGroupId())
                ));

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
                List<UserAnswerGroupedByTagResponse.UserAnswerOverallResponse> userAnswerOverallResponses = answersForTag.stream()
                        .map(userAnswerMapper::toUserAnswerGroupedByTagResponse)
                        .toList();

                groupedResponses.add(UserAnswerGroupedByTagResponse.builder()
                        .tag(tag)
                        .correctAnswers(correctAnswers)
                        .wrongAnswers(wrongAnswers)
                        .correctPercent(correctPercent)
                        .userAnswerOverallResponses(userAnswerOverallResponses)
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
                            .userAnswerOverallResponses(null)
                            .build()
            );

            // Add to the final map
            userAnswersByPart.put(partName, groupedResponses);
        }

        return userTestMapper.toTestResultResponse(userTest, userAnswersByPart);
    }

    @Override
    public Map<String, List<UserAnswerOverallResponse>> getUserAnswersGroupedByPart(String email, Long userTestId) {
        UserTest userTest = userTestRepository.findByIdWithAnswersAndQuestions(userTestId)
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

        // Fetch all questions involved in the answers
        List<Long> questionIds = answers.stream()
                .map(UserAnswerRequest::getQuestionId)
                .distinct()
                .toList();

        // Retrieve questions from the database
        List<Question> questions = questionService.getQuestionEntitiesByIds(questionIds);

        // Create a map for quick lookup
        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        for (UserAnswerRequest answerRequest : answers) {
            Question question = questionMap.get(answerRequest.getQuestionId());
            if (question == null) {
                throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question");
            }

            boolean isCorrect = answerRequest.getAnswer() != null &&
                    answerRequest.getAnswer().equals(question.getCorrectOption());

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

        // Get all group IDs
        Set<Long> groupIds = groupedByGroupId.keySet();

        // Fetch all question groups with their questions
        Map<Long, QuestionGroup> groupMap = questionGroupService.findAllByIdsWithQuestions(groupIds).stream()
                .collect(Collectors.toMap(QuestionGroup::getId, g -> g));

        // Process each group
        for (Map.Entry<Long, List<UserAnswerRequest>> entry : groupedByGroupId.entrySet()) {
            Long groupId = entry.getKey();
            List<UserAnswerRequest> groupAnswers = entry.getValue();

            // Fetch question group
            QuestionGroup questionGroup = groupMap.get(groupId);
            if (questionGroup == null) {
                throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question group");
            }

            boolean isListeningPart = questionGroupService.isListeningPart(questionGroup.getPart());

            // Create a map of questionId to Question for quick lookup
            Map<Long, Question> questionMap = questionGroup.getQuestions().stream()
                    .collect(Collectors.toMap(Question::getId, q -> q));

            // Evaluate each answer in the group
            for (UserAnswerRequest answerRequest : groupAnswers) {
                // Fetch the corresponding question
                Question question = questionMap.get(answerRequest.getQuestionId());
                if (question == null) {
                    throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question");
                }

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

    @Override
    public LearnerTestPartsResponse getTestByIdAndParts(Long testId, List<Long> parts) {
        Test test = testRepository.findByIdAndStatus(testId, ETestStatus.APPROVED)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test"));

        List<LearnerTestPartResponse> partResponses = questionGroupService.getQuestionGroupsByTestIdGroupByParts(testId, parts);
        LearnerTestPartsResponse learnerTestPartsResponse = testMapper.toLearnerTestPartsResponse(test);
        learnerTestPartsResponse.setPartResponses(partResponses);
        return learnerTestPartsResponse;
    }

    @Override
    public List<LearnerTestHistoryResponse> allLearnerTestHistories(Long testId, String email) {
        return testRepository.getLearnerTestHistoryByTest_IdAndUser_Email(testId, email);
    }
}
