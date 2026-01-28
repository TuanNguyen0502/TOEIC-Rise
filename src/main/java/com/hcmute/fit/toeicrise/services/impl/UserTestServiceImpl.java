package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.utils.HelperUtil;
import com.hcmute.fit.toeicrise.dtos.requests.useranswer.UserAnswerRequest;
import com.hcmute.fit.toeicrise.dtos.requests.usertest.PartStats;
import com.hcmute.fit.toeicrise.dtos.requests.usertest.ScoreAccumulator;
import com.hcmute.fit.toeicrise.dtos.requests.usertest.TagStats;
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
import com.hcmute.fit.toeicrise.models.enums.*;
import com.hcmute.fit.toeicrise.models.mappers.*;
import com.hcmute.fit.toeicrise.repositories.UserRepository;
import com.hcmute.fit.toeicrise.repositories.UserTestRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionGroupService;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionService;
import com.hcmute.fit.toeicrise.services.interfaces.ITestService;
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
    private final ITestService testService;
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
    private static final int MAX_FULL_TEST_RESULT_SIZE = 10;

    @Override
    public TestResultResponse getUserTestResultById(String email, Long userTestId) {
        UserTest userTest = findById(userTestId);
        checkUserTestEqualEmail(email, userTest);

        Map<String, List<UserAnswerGroupedByTagResponse>> groupedAnswers = groupUserAnswersByPartAndTag(userTest);
        return userTestMapper.toTestResultResponse(userTest, groupedAnswers);
    }

    @Override
    public Map<String, List<UserAnswerOverallResponse>> getUserAnswersGroupedByPart(String email, Long userTestId) {
        UserTest userTest = findById(userTestId);
        checkUserTestEqualEmail(email, userTest);
        List<UserAnswer> answers = userTest.getUserAnswers();
        if (answers == null || answers.isEmpty())
            return Map.of();
        Set<Long> groupIds = answers.stream().map(UserAnswer::getQuestionGroupId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> partNamesByGroupId = questionGroupService.getPartNamesByQuestionGroupIds(groupIds);
        Map<String, List<UserAnswerOverallResponse>> groupedAnswers = new HashMap<>();

        for (UserAnswer userAnswer : answers) {
            String partName = partNamesByGroupId.get(userAnswer.getQuestionGroupId());
            if (partName == null)
                throw new AppException(ErrorCode.INVALID_REQUEST, "Part name invalid");
            groupedAnswers.computeIfAbsent(partName, _ -> new ArrayList<>()).add(userAnswerMapper.toUserAnswerOverallResponse(userAnswer));
        }
        return groupedAnswers;
    }

    @Transactional
    @Override
    @CacheEvict(value = "systemOverview", key = "'global'")
    public TestResultOverallResponse calculateAndSaveUserTestResult(String email, UserTestRequest request) {
        User user = userRepository.findByAccount_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User"));
        Test test = testService.getTestById(request.getTestId());
        testService.incrementNumberOfLearnersSubmit(test);

        List<Long> questionGroupIds = request.getAnswers().stream().map(UserAnswerRequest::getQuestionGroupId).distinct().toList();
        questionGroupService.checkQuestionGroupsExistByIds(questionGroupIds);

        UserTest userTest = UserTest.builder()
                .user(user)
                .test(test)
                .totalQuestions(request.getAnswers().size())
                .timeSpent(request.getTimeSpent())
                .parts(request.getParts())
                .build();
        calculate(userTest, request.getAnswers());
        userTestRepository.save(userTest);
        return userTestMapper.toTestResultOverallResponse(userTest);
    }

    @Override
    public LearnerTestPartsResponse getTestByIdAndParts(Long testId, List<Long> parts) {
        Test test = testService.getTestByIdAndStatus(testId, ETestStatus.APPROVED);

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
                EPart part = EPart.getEPart(partName);
                EExamType examType = part.isRequiredAudio() ? EExamType.LISTENING : EExamType.READING;

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

        ExamTypeStatsResponse listening = new ExamTypeStatsResponse();
        listening.buildExamTypeStatsResponse(totalQuestionsListening, correctAnswersListening,
                rawDataByExamType.get(EExamType.LISTENING), rawPartStatsByExamType.get(EExamType.LISTENING));

        ExamTypeStatsResponse reading = new ExamTypeStatsResponse();
        reading.buildExamTypeStatsResponse(totalQuestionsReading, correctAnswersReading,
                rawDataByExamType.get(EExamType.READING), rawPartStatsByExamType.get(EExamType.READING));

        return AnalysisResultResponse.builder()
                .numberOfTests(numberOfTests)
                .numberOfSubmissions(userTests.size())
                .totalTimes(totalSpent)
                .examList(List.of(listening, reading))
                .build();
    }
  
    @Override
    public PageResponse getAllHistories(Specification<UserTest> userTestSpecification, Pageable pageable) {
        Page<LearnerTestHistoryResponse> learnerTestResponses = userTestRepository.findAll(userTestSpecification, pageable).map(userTestMapper::toLearnerTestHistoryResponse);
        return pageResponseMapper.toPageResponse(learnerTestResponses);
    }

    @Override
    public FullTestResultResponse getFullTestResult(String email, int size) {
        int limit = Math.min(Math.max(size, 1), MAX_FULL_TEST_RESULT_SIZE);
        Pageable pageable = PageRequest.of(0, limit);
        List<UserTest> userTests = userTestRepository.findByUser_Account_EmailAndTest_StatusAndTotalScoreIsNotNullOrderByCreatedAtDesc(email, pageable, ETestStatus.APPROVED);
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

        int averageScore = HelperUtil.initialAverageValue(scores);
        int maxScore = HelperUtil.initialMaxValue(scores);
        int averageListeningScore = HelperUtil.initialAverageValue(listeningScore);
        int averageReadingScore = HelperUtil.initialAverageValue(readingScore);
        int maxListeningScore = HelperUtil.initialMaxValue(listeningScore);
        int maxReadingScore = HelperUtil.initialMaxValue(readingScore);

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

    private UserTest findById(Long id) {
        return userTestRepository.findByIdWithAnswersAndQuestions(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "UserTest"));
    }

    private void checkUserTestEqualEmail(String email, UserTest userTest) {
        if (email == null)
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        if (userTest == null)
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test Result");
        if (!userTest.getUser().getAccount().getEmail().equals(email))
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test Result");
    }

    private Map<String, List<UserAnswerGroupedByTagResponse>> groupUserAnswersByPartAndTag(UserTest userTest){
        List<UserAnswer> userAnswers = userTest.getUserAnswers();
        if (userAnswers == null || userAnswers.isEmpty())
            return Collections.emptyMap();

        Set<Long> questionGroupIds = userAnswers.stream().map(UserAnswer::getQuestionGroupId).filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> partNamesByGroupId = questionGroupService.getPartNamesByQuestionGroupIds(questionGroupIds);

        return userAnswers.stream().collect((Collectors.groupingBy((UserAnswer ua) -> {
            String partName = partNamesByGroupId.get(ua.getQuestionGroupId());
            if (partName == null)
                throw new AppException(ErrorCode.INVALID_REQUEST, "Part name invalid");
            return partName;
            }, Collectors.collectingAndThen(Collectors.toList(), this::groupByTag))));
    }

    private List<UserAnswerGroupedByTagResponse> groupByTag(List<UserAnswer> answersInPart){
        if (answersInPart == null || answersInPart.isEmpty())
            return List.of();

        Map<String, List<UserAnswer>> answersByTag = answersInPart.stream().flatMap(ua -> {
            if (ua.getQuestion() == null||ua.getQuestion().getTags() == null)
                return Stream.empty();
            return ua.getQuestion().getTags().stream().map(tag -> Map.entry(tag.getName(), ua));
        }).collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, toList())));
        List<UserAnswerGroupedByTagResponse> groupedByTag = new ArrayList<>();
        for (Map.Entry<String, List<UserAnswer>> entry : answersByTag.entrySet()) {
            List<UserAnswer> userAnswers = entry.getValue();
            int correct = (int) userAnswers.stream().filter(UserAnswer::getIsCorrect).count();
            int wrong = userAnswers.size() - correct;
            double percentage = userAnswers.isEmpty() ? 0.0 : (correct * 100.0 / userAnswers.size());
            groupedByTag.add(UserAnswerGroupedByTagResponse.builder()
                    .tag(entry.getKey())
                    .correctAnswers(correct)
                    .wrongAnswers(wrong)
                    .correctPercent(percentage)
                    .userAnswerOverallResponses(userAnswers.stream().map(userAnswerMapper::toUserAnswerGroupedByTagResponse).toList())
                    .build());
        }
        int totalCorrect = (int) answersInPart.stream().filter(UserAnswer::getIsCorrect).count();
        int total = answersInPart.size();
        double totalPercent = total == 0 ? 0.0 : (totalCorrect * 100.0 / total);
        groupedByTag.add(UserAnswerGroupedByTagResponse.builder()
                .tag("Total")
                .correctAnswers(totalCorrect)
                .wrongAnswers(total - totalCorrect)
                .correctPercent(totalPercent)
                .userAnswerOverallResponses(null)
                .build());
        return groupedByTag;
    }

    private void calculate(UserTest userTest, List<UserAnswerRequest> answerRequests){
        boolean isExamMode = userTest.getParts() == null || userTest.getParts().isEmpty();
        ScoreAccumulator accumulator = new ScoreAccumulator();

        if (isExamMode) {
            Map<Long, List<UserAnswerRequest>> groupedByGroupId = answerRequests.stream().collect(
                    Collectors.groupingBy(UserAnswerRequest::getQuestionGroupId));
            Set<Long> questionGroupIds = groupedByGroupId.keySet();
            Map<Long, QuestionGroup> groupMap = questionGroupService.findAllByIdsWithQuestions(questionGroupIds).stream()
                    .collect(Collectors.toMap(QuestionGroup::getId, g -> g));

            for (Map.Entry<Long, List<UserAnswerRequest>> e : groupedByGroupId.entrySet()){
                QuestionGroup questionGroup = groupMap.get(e.getKey());
                if (questionGroup == null)
                    throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question group");
                boolean isListening = questionGroupService.isListeningPart(questionGroup.getPart());
                Map<Long, Question> questionMap = questionGroup.getQuestions().stream().collect(Collectors.toMap(Question::getId, q -> q));

                for (UserAnswerRequest userAnswerRequest : e.getValue()) {
                    Question question = questionMap.get(userAnswerRequest.getQuestionId());
                    if (question == null)
                        throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question");
                    calculatorScore(userAnswerRequest,question, accumulator, isListening, userTest);
                }
            }
            int listenScore = estimatedListeningScoreMap.getOrDefault(accumulator.getListeningCorrectAnswers(), 0);
            int readingScore = estimatedReadingScoreMap.getOrDefault(accumulator.getReadingCorrectAnswers(), 0);
            userTest.setListeningScore(listenScore);
            userTest.setReadingScore(readingScore);
            userTest.setTotalScore(listenScore + readingScore);
        }
        else {
            List<Long> questionIds = answerRequests.stream().map(UserAnswerRequest::getQuestionId).distinct().toList();
            Map<Long, Question> questionMap = questionService.getQuestionEntitiesByIds(questionIds).stream()
                    .collect(Collectors.toMap(Question::getId, q -> q));

            for (UserAnswerRequest userAnswerRequest : answerRequests) {
                Question question = questionMap.get(userAnswerRequest.getQuestionId());
                if (question == null)
                    throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question");
                boolean isListening = questionGroupService.isListeningPart(question.getQuestionGroup().getPart());
                calculatorScore(userAnswerRequest, question, accumulator, isListening, userTest);
            }
        }
        userTest.setCorrectAnswers(accumulator.getCorrectAnswers());
        userTest.setCorrectPercent(answerRequests.isEmpty() ? 0.0 : (accumulator.getCorrectAnswers() * 100.0 / answerRequests.size()));
        userTest.setListeningCorrectAnswers(accumulator.getListeningCorrectAnswers());
        userTest.setReadingCorrectAnswers(accumulator.getReadingCorrectAnswers());
        userTest.setTotalListeningQuestions(accumulator.getListeningTotal());
        userTest.setTotalReadingQuestions(accumulator.getReadingTotal());
    }

    private void calculatorScore(UserAnswerRequest userAnswerRequest, Question question, ScoreAccumulator accumulator,
                                 boolean isListening, UserTest userTest){
        boolean correct = userAnswerRequest.getAnswer() != null && userAnswerRequest.getAnswer().equals(question.getCorrectOption());
        if (correct){
            accumulator.setCorrectAnswers(accumulator.getCorrectAnswers() + 1);
            if (isListening)
                accumulator.setListeningCorrectAnswers(accumulator.getListeningCorrectAnswers() + 1);
            else accumulator.setReadingCorrectAnswers(accumulator.getReadingCorrectAnswers() + 1);
        }
        if (isListening)
            accumulator.setListeningTotal(accumulator.getListeningTotal() + 1);
        else accumulator.setReadingTotal(accumulator.getReadingTotal() + 1);
        addUserAnswer(userTest, userAnswerRequest, question, correct);
    }

    private void addUserAnswer(UserTest userTest, UserAnswerRequest userAnswerRequest, Question question, boolean correct) {
        userTest.getUserAnswers().add(UserAnswer.builder()
                .userTest(userTest)
                .question(question)
                .questionGroupId(userAnswerRequest.getQuestionGroupId())
                .answer(userAnswerRequest.getAnswer())
                .isCorrect(correct)
                .build());
    }
}
