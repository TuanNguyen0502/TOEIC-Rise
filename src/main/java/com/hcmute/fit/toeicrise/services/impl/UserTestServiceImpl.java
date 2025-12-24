package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.dtos.requests.useranswer.UserAnswerRequest;
import com.hcmute.fit.toeicrise.dtos.requests.usertest.UserTestRequest;
import com.hcmute.fit.toeicrise.dtos.responses.analysis.AnalysisResultResponse;
import com.hcmute.fit.toeicrise.dtos.responses.analysis.ExamTypeFullTestResponse;
import com.hcmute.fit.toeicrise.dtos.responses.analysis.ExamTypeStatsResponse;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.analysis.FullTestResultResponse;
import com.hcmute.fit.toeicrise.dtos.responses.statistic.ActivityPointResponse;
import com.hcmute.fit.toeicrise.dtos.responses.statistic.ActivityTrendResponse;
import com.hcmute.fit.toeicrise.dtos.responses.statistic.ScoreDistInsightResponse;
import com.hcmute.fit.toeicrise.dtos.responses.statistic.TestModeInsightResponse;
import com.hcmute.fit.toeicrise.dtos.responses.usertest.TestResultOverallResponse;
import com.hcmute.fit.toeicrise.dtos.responses.usertest.TestResultResponse;
import com.hcmute.fit.toeicrise.dtos.responses.usertest.UserAnswerGroupedByTagResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.*;
import com.hcmute.fit.toeicrise.dtos.responses.useranswer.UserAnswerOverallResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.*;
import com.hcmute.fit.toeicrise.models.enums.EDays;
import com.hcmute.fit.toeicrise.models.enums.EExamType;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.*;
import com.hcmute.fit.toeicrise.repositories.TestRepository;
import com.hcmute.fit.toeicrise.repositories.UserRepository;
import com.hcmute.fit.toeicrise.repositories.UserTestRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionGroupService;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionService;
import com.hcmute.fit.toeicrise.services.interfaces.IUserTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

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
    private final PartMapper partMapper;
    private final QuestionGroupMapper questionGroupMapper;
    private final PageResponseMapper pageResponseMapper;

    private final Map<Integer, Integer> estimatedReadingScoreMap = Constant.estimatedReadingScoreMap;
    private final Map<Integer, Integer> estimatedListeningScoreMap = Constant.estimatedListeningScoreMap;

    @Override
    public TestResultResponse getUserTestResultById(String email, Long userTestId) {
        UserTest userTest = userTestRepository.findByIdWithAnswersAndQuestions(userTestId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "UserTest"));

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
                            Collectors.mapping(Map.Entry::getValue, toList())
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
    @CacheEvict(value = "systemOverview", key = "'global'")
    public TestResultOverallResponse calculateAndSaveUserTestResult(String email, UserTestRequest request) {
        User user = userRepository.findByAccount_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User"));
        Test test = testRepository.findById(request.getTestId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test"));
        test.setNumberOfLearnerTests(test.getNumberOfLearnerTests() + 1);
        testRepository.save(test);

        // Validate that all question groups in the answers exist
        List<Long> questionGroupIds = request.getAnswers().stream()
                .map(UserAnswerRequest::getQuestionGroupId)
                .distinct()
                .toList();
        questionGroupService.checkQuestionGroupsExistByIds(questionGroupIds);

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
        int listeningQuestion = 0;
        int readingQuestion = 0;
        int listeningCorrectAnswers = 0;
        int readingCorrectAnswers = 0;

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

            boolean isListeningPart = questionGroupService.isListeningPart(question.getQuestionGroup().getPart());
            if (isListeningPart){
                listeningQuestion++;
                if (isCorrect)
                    listeningCorrectAnswers++;
            }
            else {
                readingQuestion++;
                if (isCorrect)
                    readingCorrectAnswers++;
            }
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
        userTest.setTotalListeningQuestions(listeningQuestion);
        userTest.setTotalReadingQuestions(readingQuestion);
        userTest.setReadingCorrectAnswers(readingCorrectAnswers);
        userTest.setListeningCorrectAnswers(listeningCorrectAnswers);
        userTest.setCorrectPercent(((double) correctAnswers / answers.size()) * 100);
    }

    private void calculateExamScore(UserTest userTest, List<UserAnswerRequest> answers) {
        int correctAnswers = 0;
        int listeningCorrect = 0;
        int readingCorrect = 0;
        int listeningQuestion = 0;
        int readingQuestion = 0;

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
                if (isListeningPart)
                    listeningQuestion++;
                else readingQuestion++;
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
        userTest.setCorrectPercent(((double) correctAnswers / answers.size()) * 100);
        userTest.setListeningCorrectAnswers(listeningCorrect);
        userTest.setReadingCorrectAnswers(readingCorrect);
        userTest.setListeningScore(estimatedListeningScoreMap.get(listeningCorrect));
        userTest.setReadingScore(estimatedReadingScoreMap.get(readingCorrect));
        userTest.setTotalScore(userTest.getListeningScore() + userTest.getReadingScore());
        userTest.setTotalListeningQuestions(listeningQuestion);
        userTest.setTotalReadingQuestions(readingQuestion);
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
    public LearnerTestPartsResponse getUserTestDetail(Long userTestId, String email) {
        UserTest userTest = userTestRepository.findUserTestById(userTestId, email).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User test"));
        LearnerTestPartsResponse learnerTestPartsResponse = testMapper.toLearnerTestPartsResponse(userTest.getTest());
        Map<Part, List<UserAnswer>> answerByPart = userTest.getUserAnswers().stream()
                .collect(Collectors.groupingBy(ua -> ua.getQuestion().getQuestionGroup().getPart()));
        List<LearnerTestPartResponse> partResponses = answerByPart.entrySet()
                .stream().map(entry -> {
                    Part part = entry.getKey();
                    List<UserAnswer> answers = entry.getValue();
                    Map<QuestionGroup, List<UserAnswer>> answerByQuestionGroups = answers.stream()
                            .collect(Collectors.groupingBy(ua -> ua.getQuestion().getQuestionGroup()));

                    List<LearnerTestQuestionGroupResponse> questionGroupResponses = answerByQuestionGroups.entrySet().stream()
                            .sorted(Comparator.comparing(e -> e.getKey().getPosition()))
                            .map(groupEntry -> {
                                QuestionGroup questionGroup = groupEntry.getKey();
                                List<UserAnswer> userAnswers = groupEntry.getValue();

                                List<LearnerAnswerResponse> questionAndAnswers = userAnswers.stream()
                                        .sorted(Comparator.comparing(ua -> ua.getQuestion().getPosition()))
                                        .map(userAnswerMapper::toLearnerAnswerResponse)
                                        .toList();
                                List<Object> questionsAsObject = new ArrayList<>(questionAndAnswers);
                                LearnerTestQuestionGroupResponse questionGroupResponse = questionGroupMapper.toLearnerTestQuestionGroupResponse(questionGroup);
                                questionGroupResponse.setQuestions(questionsAsObject);
                                return questionGroupResponse;

                            }).toList();
                    LearnerTestPartResponse partResponse = partMapper.toLearnerTestPartResponse(part);
                    partResponse.setQuestionGroups(questionGroupResponses);
                    return partResponse;
                }).sorted(Comparator.comparing(LearnerTestPartResponse::getPartName))
                .toList();
        learnerTestPartsResponse.setPartResponses(partResponses);
        return learnerTestPartsResponse;
    }

    @Override
    public AnalysisResultResponse getAnalysisResult(String email, EDays days) {
        Optional<UserTest> userTest = userTestRepository.findFirstByOrderByCreatedAtDesc();
        LocalDateTime localDateTime = userTest.map(user -> user.getCreatedAt().minusDays(days.getDays()))
                .orElseGet(() -> LocalDateTime.now().minusDays(days.getDays()));
        List<UserTest> userTests = userTestRepository.findAllAnalysisResult(email, localDateTime, ETestStatus.APPROVED);
        int numberOfTests = (int)userTests.stream().map(ut -> ut.getTest() != null ? ut.getTest().getId() : null)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        Set<Long> questionIds = userTests.stream()
                .filter(ut -> ut.getUserAnswers() != null &&!ut.getUserAnswers().isEmpty())
                .flatMap(ut -> ut.getUserAnswers().stream())
                .map(ua -> ua.getQuestion() != null ? ua.getQuestion().getId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, Question> questionMapWithTags;
        if (!questionIds.isEmpty()) {
            List<Question> questionsWithTags = questionService.findAllQuestionByIdWithTags(questionIds);
            questionMapWithTags = questionsWithTags.stream().collect(Collectors.toMap(Question::getId, q -> q));
        } else {
            questionMapWithTags = new HashMap<>();
        }
        userTests.forEach(ut -> {
            if (ut.getUserAnswers() != null) {
                ut.getUserAnswers().forEach(userAnswer -> {
                    if (userAnswer.getQuestion() != null) {
                        Question questionWithTags = questionMapWithTags.get(userAnswer.getQuestion().getId());
                        if (questionWithTags != null && questionWithTags.getTags() != null) {
                            userAnswer.getQuestion().setTags(questionWithTags.getTags());
                        }
                    }
                });
            }
        });
        Set<Long> allQuestionGroupIds = userTests.stream()
                .filter(ut -> ut.getUserAnswers() != null && !ut.getUserAnswers().isEmpty())
                .flatMap(ut -> ut.getUserAnswers().stream())
                .map(UserAnswer::getQuestionGroupId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> partNamesByGroupId = questionGroupService.getPartNamesByQuestionGroupIds(allQuestionGroupIds);

        Map<EExamType, Map<String, Map<String, TagStats>>> rawDataByExamType = new EnumMap<>(EExamType.class);
        Map<EExamType, Map<String, PartStats>> rawPartStatsByExamType = new EnumMap<>(EExamType.class);

        for (EExamType examType : EExamType.values()) {
            rawDataByExamType.put(examType, new HashMap<>());
            rawPartStatsByExamType.put(examType, new HashMap<>());
        }

        long totalSpent = 0L;
        int totalQuestionsListening = 0;
        int correctAnswersListening = 0;
        int totalQuestionsReading = 0;
        int correctAnswersReading = 0;

        for (UserTest ut : userTests) {
            totalSpent += ut.getTimeSpent() != null ? ut.getTimeSpent() : 0;
            totalQuestionsListening += ut.getTotalListeningQuestions() != null ? ut.getTotalListeningQuestions() : 0;
            correctAnswersListening += ut.getListeningCorrectAnswers() != null ? ut.getListeningCorrectAnswers() : 0;
            totalQuestionsReading += ut.getTotalReadingQuestions() != null ? ut.getTotalReadingQuestions() : 0;
            correctAnswersReading += ut.getReadingCorrectAnswers() != null ? ut.getReadingCorrectAnswers() : 0;
            
            List<UserAnswer> userAnswers = ut.getUserAnswers();
            if (userAnswers == null || userAnswers.isEmpty()) continue;

            Map<String, List<UserAnswer>> answersByPart = userAnswers.stream()
                    .collect(Collectors.groupingBy(ua ->
                            partNamesByGroupId.get(ua.getQuestionGroupId())
                    ));

            for (Map.Entry<String, List<UserAnswer>> entry : answersByPart.entrySet()) {
                String partName = entry.getKey();
                List<UserAnswer> answersInPart = entry.getValue();

                if (partName == null) continue;

                EExamType examType = isListeningPart(partName) ? EExamType.LISTENING : EExamType.READING;

                Map<String, Map<String, TagStats>> examTypeRawData = rawDataByExamType.get(examType);
                Map<String, PartStats> examTypePartStats = rawPartStatsByExamType.get(examType);

                examTypeRawData.computeIfAbsent(partName,_ -> new HashMap<>());
                examTypePartStats.computeIfAbsent(partName,_ -> new PartStats());

                Map<String, List<UserAnswer>> answersByTag = answersInPart.stream()
                        .flatMap(ua -> {
                            if (ua.getQuestion() != null && ua.getQuestion().getTags() != null) {
                                return ua.getQuestion().getTags().stream()
                                        .map(tag -> Map.entry(tag.getName(), ua));
                            }
                            return Stream.empty();
                        })
                        .collect(Collectors.groupingBy(
                                Map.Entry::getKey,
                                Collectors.mapping(Map.Entry::getValue, toList())
                        ));

                Map<String, TagStats> tagStatsMap = examTypeRawData.get(partName);
                answersByTag.forEach((tagName, answersForTag) -> {
                    int correct = (int) answersForTag.stream().filter(UserAnswer::getIsCorrect).count();
                    int wrong = answersForTag.size() - correct;

                    TagStats tagStats = tagStatsMap.computeIfAbsent(tagName,_ -> new TagStats());
                    tagStats.add(correct, wrong);
                });

                int partCorrect = (int) answersInPart.stream().filter(UserAnswer::getIsCorrect).count();
                int partWrong = answersInPart.size() - partCorrect;

                PartStats partStats = examTypePartStats.get(partName);
                partStats.add(partCorrect, partWrong);
            }
        }

        ExamTypeStatsResponse listening = buildExamTypeStatsResponse(
                totalQuestionsListening,
                correctAnswersListening,
                rawDataByExamType.get(EExamType.LISTENING),
                rawPartStatsByExamType.get(EExamType.LISTENING)
        );

        ExamTypeStatsResponse reading = buildExamTypeStatsResponse(
                totalQuestionsReading,
                correctAnswersReading,
                rawDataByExamType.get(EExamType.READING),
                rawPartStatsByExamType.get(EExamType.READING)
        );

        return AnalysisResultResponse.builder()
                .numberOfTests(numberOfTests)
                .numberOfSubmissions(userTests.size())
                .totalTimes(totalSpent)
                .examList(List.of(listening, reading))
                .build();
    }

    private boolean isListeningPart(String partName) {
        return partName != null && (
                partName.contains("1") ||
                partName.contains("2") ||
                partName.contains("3") ||
                partName.contains("4")
        );
    }

    private static class TagStats {
        int correct = 0;
        int wrong = 0;
        void add(int correctDelta, int wrongDelta) {
            this.correct += correctDelta;
            this.wrong += wrongDelta;
        }
    }

    private static class PartStats {
        int correct = 0;
        int wrong = 0;
        void add(int correctDelta, int wrongDelta) {
            this.correct += correctDelta;
            this.wrong += wrongDelta;
        }
    }

    private ExamTypeStatsResponse buildExamTypeStatsResponse(
            int totalQuestionExamType,
            int correctAnswerExamType,
            Map<String, Map<String, TagStats>> rawDataByPart,
            Map<String, PartStats> rawPartStats
    ) {
        Map<String, List<UserAnswerGroupedByTagResponse>> userAnswersByPart = new HashMap<>();
        double overallCorrectPercent = totalQuestionExamType == 0 ? 0.0 : ((double) correctAnswerExamType / totalQuestionExamType) * 100;

        if (rawDataByPart != null) {
            for (Map.Entry<String, Map<String, TagStats>> partEntry : rawDataByPart.entrySet()) {
                String partName = partEntry.getKey();
                Map<String, TagStats> tagStatsMap = partEntry.getValue();

                List<UserAnswerGroupedByTagResponse> groupedResponses = new ArrayList<>();
                UserAnswerGroupedByTagResponse totalPartResponse = null;

                tagStatsMap.forEach((tag, stats) -> {
                    int total = stats.correct + stats.wrong;
                    double correctPercent = total == 0 ? 0.0 : ((double) stats.correct / total) * 100;
                    groupedResponses.add(UserAnswerGroupedByTagResponse.builder()
                            .tag(tag)
                            .correctAnswers(stats.correct)
                            .wrongAnswers(stats.wrong)
                            .correctPercent(correctPercent)
                            .userAnswerOverallResponses(null)
                            .build());
                });
                PartStats partStats = rawPartStats.get(partName);
                if (partStats != null) {
                    int totalForPart = partStats.correct + partStats.wrong;
                    double totalPercent = totalForPart == 0 ? 0.0 : ((double) partStats.correct / totalForPart) * 100;

                    totalPartResponse = UserAnswerGroupedByTagResponse.builder()
                            .tag("Total")
                            .correctAnswers(partStats.correct)
                            .wrongAnswers(partStats.wrong)
                            .correctPercent(totalPercent)
                            .userAnswerOverallResponses(null)
                            .build();
                }
                groupedResponses.sort(Comparator.comparing(UserAnswerGroupedByTagResponse::getCorrectPercent));
                groupedResponses.add(totalPartResponse);
                userAnswersByPart.put(partName, groupedResponses);

            }
        }

        return ExamTypeStatsResponse.builder()
                .totalCorrectAnswers(correctAnswerExamType)
                .totalQuestions(totalQuestionExamType)
                .correctPercent(overallCorrectPercent)
                .userAnswersByPart(userAnswersByPart)
                .build();
    }
  
    @Override
    public PageResponse getAllHistories(Specification<UserTest> userTestSpecification, Pageable pageable) {
        Page<LearnerTestHistoryResponse> learnerTestResponses = userTestRepository.findAll(userTestSpecification, pageable).map(userTestMapper::toLearnerTestHistoryResponse);
        return pageResponseMapper.toPageResponse(learnerTestResponses);
    }

    @Override
    public FullTestResultResponse getFullTestResult(String email, int size) {
        if (size > 10)
            size = 10;
        Pageable limit = PageRequest.of(0, size);
        List<UserTest> userTests = userTestRepository.findByUser_Account_EmailAndTest_StatusAndTotalScoreIsNotNullOrderByCreatedAtDesc(email, limit, ETestStatus.APPROVED);
        List<ExamTypeFullTestResponse> examTypeFullTestResponses = new ArrayList<>();

        List<Integer> scores = new ArrayList<>();
        List<Integer> listeningScore = new ArrayList<>();
        List<Integer> readingScore = new ArrayList<>();

        for (UserTest ut : userTests) {
            scores.add(ut.getTotalScore());
            listeningScore.add(ut.getListeningScore());
            readingScore.add(ut.getReadingScore());
            ExamTypeFullTestResponse examTypeFullTestResponse = userTestMapper.toExamTypeFullTestResponse(ut);
            examTypeFullTestResponses.add(examTypeFullTestResponse);
        }

        int averageScore = scores.isEmpty() ? 0 :
                (int) scores.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        int maxScore = scores.isEmpty() ? 0 :
                scores.stream().mapToInt(Integer::intValue).max().orElse(0);
        int averageListeningScore = listeningScore.isEmpty() ? 0 :
                (int) listeningScore.stream().mapToInt(Integer::intValue).average().orElse(0);
        int averageReadingScore = readingScore.isEmpty() ? 0 :
                (int) readingScore.stream().mapToInt(Integer::intValue).average().orElse(0);
        int maxListeningScore = listeningScore.isEmpty() ? 0 :
                listeningScore.stream().mapToInt(Integer::intValue).max().orElse(0);
        int maxReadingScore = readingScore.isEmpty() ? 0 :
                readingScore.stream().mapToInt(Integer::intValue).max().orElse(0);

        return FullTestResultResponse.builder()
                .averageScore(roundToNearest5(averageScore))
                .highestScore(maxScore)
                .averageListeningScore(roundToNearest5(averageListeningScore))
                .averageReadingScore(roundToNearest5(averageReadingScore))
                .maxListeningScore(maxListeningScore)
                .maxReadingScore(maxReadingScore)
                .examTypeFullTestResponses(examTypeFullTestResponses)
                .build();
    }

    @Override
    public Long totalUserTest() {
        return userTestRepository.count();
    }

    @Override
    public ActivityTrendResponse getActivityTrend(LocalDateTime from, LocalDateTime to) {
        List<ActivityPointResponse> activityPointResponses = userTestRepository.getActivityTrend(from, to).stream().map(item ->
            new ActivityPointResponse(((java.sql.Date)item[0]).toLocalDate(), ((Number)item[1]).longValue())).toList();
        Map<LocalDate, Long> submissionsByDate = activityPointResponses.stream().collect(
                Collectors.toMap(ActivityPointResponse::getDate, ActivityPointResponse::getSubmissions));
        List<ActivityPointResponse> responses = new ArrayList<>();
        LocalDate current = from.toLocalDate();
        LocalDate end = to.toLocalDate().minusDays(1);
        long sum = 0L;

        while (!current.isAfter(end)){
            long count = submissionsByDate.getOrDefault(current, 0L);
            sum += count;
            responses.add(new ActivityPointResponse(current, count));
            current = current.plusDays(1);
        }

        return ActivityTrendResponse.builder().totalSubmissions(sum)
                .points(responses).build();
    }

    @Override
    public TestModeInsightResponse getTestModeInsight(LocalDateTime start, LocalDateTime end) {
        TestModeInsightResponse testMode = userTestRepository.countUserTestByMode(start, end);
        double sum = testMode.getFullTest() + testMode.getPratice();
        if(sum == 0)
            return testMode;
        testMode.setFullTest(Math.round((testMode.getFullTest()/sum)*100));
        testMode.setPratice(Math.round((testMode.getPratice()/sum)*100));
        return testMode;
    }

    @Override
    public ScoreDistInsightResponse getScoreInsight(LocalDateTime start, LocalDateTime end) {
        ScoreDistInsightResponse distInsightResponse = userTestRepository.countUserTestByScore(start, end);
        double total = distInsightResponse.sum();
        if(total == 0)
            return distInsightResponse;
        distInsightResponse.setBrand0_200(Math.round((distInsightResponse.getBrand0_200()/total)*100));
        distInsightResponse.setBrand200_450(Math.round((distInsightResponse.getBrand200_450()/total)*100));
        distInsightResponse.setBrand450_750(Math.round((distInsightResponse.getBrand450_750()/total)*100));
        distInsightResponse.setBrand750_990(Math.round((distInsightResponse.getBrand750_990()/total)*100));
        return distInsightResponse;
    }

    @Override
    public Long totalUserTest(LocalDateTime from, LocalDateTime to) {
        TestModeInsightResponse testMode = userTestRepository.countUserTestByMode(from, to);
        return (long) (testMode.getFullTest() + testMode.getPratice());
    }

    private int roundToNearest5(int number) {
        return (int) (Math.round(number / 5.0) * 5);
    }

    @Override
    public List<LearnerTestHistoryResponse> allLearnerTestHistories(Long testId, String email) {
        return userTestRepository.getLearnerTestHistoryByTest_IdAndUser_Email(testId, email);
    }
}
