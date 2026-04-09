package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.utils.HelperUtil;
import com.hcmute.fit.toeicrise.dtos.requests.useranswer.UserAnswerRequest;
import com.hcmute.fit.toeicrise.dtos.requests.usertest.PartStats;
import com.hcmute.fit.toeicrise.dtos.requests.usertest.ScoreAccumulator;
import com.hcmute.fit.toeicrise.dtos.requests.usertest.TagStats;
import com.hcmute.fit.toeicrise.dtos.requests.usertest.UserTestRequest;
import com.hcmute.fit.toeicrise.dtos.requests.usertest.writing.WritingAnswerSubmissionRequest;
import com.hcmute.fit.toeicrise.dtos.requests.usertest.writing.WritingTestSubmissionRequest;
import com.hcmute.fit.toeicrise.dtos.responses.analysis.AnalysisResultResponse;
import com.hcmute.fit.toeicrise.dtos.responses.analysis.ExamTypeFullTestResponse;
import com.hcmute.fit.toeicrise.dtos.responses.analysis.ExamTypeStatsResponse;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.analysis.FullTestResultResponse;
import com.hcmute.fit.toeicrise.dtos.responses.statistic.ActivityPointResponse;
import com.hcmute.fit.toeicrise.dtos.responses.statistic.ActivityTrendResponse;
import com.hcmute.fit.toeicrise.dtos.responses.statistic.ScoreDistInsightResponse;
import com.hcmute.fit.toeicrise.dtos.responses.statistic.TestModeInsightResponse;
import com.hcmute.fit.toeicrise.dtos.responses.usertest.RetestResultOverallResponse;
import com.hcmute.fit.toeicrise.dtos.responses.usertest.TestResultOverallResponse;
import com.hcmute.fit.toeicrise.dtos.responses.usertest.TestResultResponse;
import com.hcmute.fit.toeicrise.dtos.responses.usertest.UserAnswerGroupedByTagResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.*;
import com.hcmute.fit.toeicrise.dtos.responses.useranswer.UserAnswerOverallResponse;
import com.hcmute.fit.toeicrise.dtos.responses.usertest.writing.WritingTestResultOverallResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.*;
import com.hcmute.fit.toeicrise.models.enums.*;
import com.hcmute.fit.toeicrise.models.mappers.*;
import com.hcmute.fit.toeicrise.repositories.UserTestRepository;
import com.hcmute.fit.toeicrise.services.interfaces.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UserTestServiceImpl implements IUserTestService {
    private static final int MAX_FULL_TEST_RESULT_SIZE = 10;
    private final IQuestionService questionService;
    private final IQuestionGroupService questionGroupService;
    private final ITestService testService;
    private final IUserService userService;
    private final UserTestRepository userTestRepository;
    private final UserTestMapper userTestMapper;
    private final TestMapper testMapper;
    private final UserAnswerMapper userAnswerMapper;
    private final PartMapper partMapper;
    private final QuestionGroupMapper questionGroupMapper;
    private final PageResponseMapper pageResponseMapper;
    private final QuestionMapper questionMapper;
    private final Map<Integer, Integer> estimatedReadingScoreMap = Constant.estimatedReadingScoreMap;
    private final Map<Integer, Integer> estimatedListeningScoreMap = Constant.estimatedListeningScoreMap;

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
        User user = userService.getUserByEmail(email);
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

    @Transactional
    @Override
    public WritingTestResultOverallResponse submitWritingTest(String email, WritingTestSubmissionRequest request) {
        User user = userService.getUserByEmail(email);
        Test test = testService.getTestById(request.getTestId());

        List<Long> questionIds = request.getAnswers().stream()
                .map(WritingAnswerSubmissionRequest::getQuestionId)
                .distinct()
                .toList();
        List<Question> questions = questionService.getQuestionsWithGroupsByIds(questionIds);
        Map<Long, Question> questionMap = questions.stream().collect(Collectors.toMap(Question::getId, q -> q));
        UserTest userTest = UserTest.builder()
                .user(user)
                .test(test)
                .totalQuestions(request.getAnswers().size())
                .timeSpent(request.getTimeSpent())
                .parts(request.getParts())
                .build();
        List<UserAnswer> userAnswers = new ArrayList<>();

        for (WritingAnswerSubmissionRequest answerRequest : request.getAnswers()) {
            Question question = questionMap.get(answerRequest.getQuestionId());
            UserAnswer userAnswer = UserAnswer.builder()
                    .userTest(userTest)
                    .question(question)
                    .questionGroupId(question.getQuestionGroup().getId())
                    .answerText(answerRequest.getAnswerText())
                    .isCorrect(answerRequest.getAnswerText() != null && !answerRequest.getAnswerText().isBlank())
                    .build();
            userAnswers.add(userAnswer);
        }
        userTest.setUserAnswers(userAnswers);

        testService.incrementNumberOfLearnersSubmit(test);
        userTestRepository.save(userTest);

        return userTestMapper.toWritingTestResultOverallResponse(userTest);
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
                                List<LearnerAnswerResponse> questionAndAnswers = groupEntry.getValue().stream()
                                        .sorted(Comparator.comparing(ua -> ua.getQuestion().getPosition()))
                                        .map(userAnswerMapper::toLearnerAnswerResponse)
                                        .toList();
                                List<Object> questionsAsObject = new ArrayList<>(questionAndAnswers);
                                LearnerTestQuestionGroupResponse questionGroupResponse = questionGroupMapper.toLearnerTestQuestionGroupResponse(groupEntry.getKey());
                                questionGroupResponse.setQuestions(questionsAsObject);
                                return questionGroupResponse;

                            }).toList();
                    LearnerTestPartResponse partResponse = partMapper.toLearnerTestPartResponse(part);
                    partResponse.setQuestionGroups(new ArrayList<>(questionGroupResponses));
                    return partResponse;
                }).sorted(Comparator.comparing(LearnerTestPartResponse::getPartName))
                .toList();
        learnerTestPartsResponse.setPartResponses(partResponses);
        return learnerTestPartsResponse;
    }

    @Override
    public AnalysisResultResponse getAnalysisResult(String email, EDays days) {
        LocalDateTime localDateTime = userTestRepository.findLatestUserTestCreatedAt(email)
                .map(user -> user.minusDays(days.getDays()))
                .orElseGet(() -> LocalDateTime.now().minusDays(days.getDays()));
        List<UserTest> userTests = userTestRepository.findAllAnalysisResult(email, localDateTime, ETestStatus.APPROVED);
        ExamTypeStatsResponse listening = new ExamTypeStatsResponse();
        ExamTypeStatsResponse reading = new ExamTypeStatsResponse();

        if (userTests.isEmpty())
            return AnalysisResultResponse.builder()
                    .numberOfTests(0)
                    .numberOfSubmissions(userTests.size())
                    .totalTimes(0L)
                    .examList(List.of(
                            listening.buildExamTypeStatsResponse(0, 0, Map.of(), Map.of()),
                            reading.buildExamTypeStatsResponse(0, 0, Map.of(), Map.of())))
                    .build();
        int numberOfTests = (int) userTests.stream().map(ut -> ut.getTest() != null ? ut.getTest().getId() : null)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        Set<Long> questionIds = userTests.stream()
                .filter(ut -> ut.getUserAnswers() != null && !ut.getUserAnswers().isEmpty())
                .flatMap(ut -> ut.getUserAnswers().stream())
                .map(ua -> ua.getQuestion() != null ? ua.getQuestion().getId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, Question> questionMapWithTags = questionIds.isEmpty() ? new HashMap<>() :
                questionService.findAllQuestionByIdWithTags(questionIds).stream()
                .collect(Collectors.toMap(Question::getId, q -> q));
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

                examTypeRawData.computeIfAbsent(partName, _ -> new HashMap<>());
                examTypePartStats.computeIfAbsent(partName, _ -> new PartStats());

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

                    TagStats tagStats = tagStatsMap.computeIfAbsent(tagName, _ -> new TagStats());
                    tagStats.add(correct, wrong);
                });

                int partCorrect = (int) answersInPart.stream().filter(UserAnswer::getIsCorrect).count();
                int partWrong = answersInPart.size() - partCorrect;

                PartStats partStats = examTypePartStats.get(partName);
                partStats.add(partCorrect, partWrong);
            }
        }

        listening = listening.buildExamTypeStatsResponse(totalQuestionsListening, correctAnswersListening,
                rawDataByExamType.get(EExamType.LISTENING), rawPartStatsByExamType.get(EExamType.LISTENING));

        reading = reading.buildExamTypeStatsResponse(totalQuestionsReading, correctAnswersReading,
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
        int limit = Math.clamp(size, 1, MAX_FULL_TEST_RESULT_SIZE);
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
                new ActivityPointResponse(((java.sql.Date) item[0]).toLocalDate(), ((Number) item[1]).longValue())).toList();
        Map<LocalDate, Long> submissionsByDate = activityPointResponses.stream().collect(
                Collectors.toMap(ActivityPointResponse::getDate, ActivityPointResponse::getSubmissions));
        List<ActivityPointResponse> responses = new ArrayList<>();
        LocalDate current = from.toLocalDate();
        LocalDate end = to.toLocalDate().minusDays(1);
        long sum = 0L;

        while (!current.isAfter(end)) {
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
        if (sum == 0)
            return testMode;
        testMode.setFullTest(Math.round((testMode.getFullTest() / sum) * 100));
        testMode.setPratice(Math.round((testMode.getPratice() / sum) * 100));
        return testMode;
    }

    @Override
    public ScoreDistInsightResponse getScoreInsight(LocalDateTime start, LocalDateTime end) {
        ScoreDistInsightResponse distInsightResponse = userTestRepository.countUserTestByScore(start, end);
        double total = distInsightResponse.sum();
        if (total == 0)
            return distInsightResponse;
        distInsightResponse.setBrand0_200(Math.round((distInsightResponse.getBrand0_200() / total) * 100));
        distInsightResponse.setBrand200_450(Math.round((distInsightResponse.getBrand200_450() / total) * 100));
        distInsightResponse.setBrand450_750(Math.round((distInsightResponse.getBrand450_750() / total) * 100));
        distInsightResponse.setBrand750_990(Math.round((distInsightResponse.getBrand750_990() / total) * 100));
        return distInsightResponse;
    }

    @Override
    public Long totalUserTest(LocalDateTime from, LocalDateTime to) {
        TestModeInsightResponse testMode = userTestRepository.countUserTestByMode(from, to);
        return (long) (testMode.getFullTest() + testMode.getPratice());
    }

    @Override
    @Transactional(readOnly = true)
    public LearnerTestPartsResponse getLearnerWrongAnswer(Long userTestId, String email) {
        UserTest userTest = userTestRepository.findUserTestByIdWithWrongAnswer(userTestId, email).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User test"));
        LearnerTestPartsResponse learnerTestPartsResponse = testMapper.toLearnerTestPartsResponse(userTest.getTest());

        if (userTest.getUserAnswers() == null || userTest.getUserAnswers().isEmpty())
            learnerTestPartsResponse.setPartResponses(Collections.emptyList());
        else {
            Map<Part, List<UserAnswer>> answersByPart = groupWrongAnswersByPart(userTest.getUserAnswers());
            List<LearnerTestPartResponse> partResponses = answersByPart.entrySet().stream().map(entry -> {
                Part part = entry.getKey();
                List<UserAnswer> userAnswers = entry.getValue();
                Map<QuestionGroup, List<UserAnswer>> answersByQuestionGroup = userAnswers.stream().collect(
                        Collectors.groupingBy(userAnswer -> userAnswer.getQuestion().getQuestionGroup(),
                                LinkedHashMap::new, Collectors.toList()));
                List<LearnerTestQuestionGroupWithoutTranscriptResponse> groupResponses = answersByQuestionGroup.entrySet().stream().sorted(
                                Comparator.comparing(questionGroupListEntry -> questionGroupListEntry.getKey().getPosition()))
                        .map(group -> {
                            QuestionGroup questionGroup = group.getKey();
                            List<UserAnswer> userAnswerList = group.getValue();
                            List<LearnerTestQuestionResponse> questionResponses = userAnswerList.stream().sorted(Comparator.comparing(question -> question.getQuestion().getPosition()))
                                    .map(question -> questionMapper.toLearnerTestQuestionResponse(question.getQuestion())).toList();

                            LearnerTestQuestionGroupWithoutTranscriptResponse groupResponse = questionGroupMapper.toLearnerTestQuestionGroupWithoutTranscriptResponse(questionGroup);
                            groupResponse.setQuestions(new ArrayList<>(questionResponses));
                            return groupResponse;
                        })
                        .toList();
                LearnerTestPartResponse partResponse = partMapper.toLearnerTestPartResponse(part);
                partResponse.setQuestionGroups(new ArrayList<>(groupResponses));
                return partResponse;
            }).sorted(Comparator.comparing(LearnerTestPartResponse::getPartName)).toList();

            learnerTestPartsResponse.setPartResponses(partResponses);
        }
        return learnerTestPartsResponse;
    }

    @Override
    public RetestResultOverallResponse getResultAfterSubmitWrongAnswer(Long userTestId, String email, UserTestRequest request) {
        UserTest userTest = userTestRepository.findUserTestByIdWithWrongAnswer(userTestId, email).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User test"));

        if (request == null || request.getAnswers() == null)
            throw new AppException(ErrorCode.INVALID_REQUEST);
        List<UserAnswerRequest> answers = request.getAnswers();
        Set<Long> questionIds = answers.stream().map(UserAnswerRequest::getQuestionId).collect(Collectors.toSet());
        Map<Long, Question> questionMap = questionService.getQuestionEntitiesByIds(new ArrayList<>(questionIds))
                .stream().collect(Collectors.toMap(Question::getId, q -> q));

        List<UserAnswer> retestAnswers = new ArrayList<>();
        int correctAnswers = 0;

        for (UserAnswerRequest userAnswerRequest : answers) {
            Question question = questionMap.get(userAnswerRequest.getQuestionId());
            if (question == null)
                throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question");
            boolean isCorrect = userAnswerRequest.getAnswer() != null && userAnswerRequest.getAnswer().equals(question.getCorrectOption());
            if (isCorrect)
                correctAnswers++;
            UserAnswer userAnswer = UserAnswer.builder()
                    .userTest(userTest)
                    .question(question)
                    .questionGroupId(userAnswerRequest.getQuestionGroupId())
                    .isCorrect(isCorrect)
                    .answer(userAnswerRequest.getAnswer())
                    .build();
            retestAnswers.add(userAnswer);
        }

        int totalQuestions = retestAnswers.size();
        int timeSpent = request.getTimeSpent();
        LearnerTestPartsResponse testPartsResponse = testMapper.toLearnerTestPartsResponse(userTest.getTest());
        if (retestAnswers.isEmpty())
            testPartsResponse.setPartResponses(Collections.emptyList());
        else {
            Map<Part, List<UserAnswer>> answersByPart = groupWrongAnswersByPart(retestAnswers);
            List<LearnerTestPartResponse> partResponses = answersByPart.entrySet().stream().map(entry -> {
                Part part = entry.getKey();
                List<UserAnswer> userAnswers = entry.getValue();
                Map<QuestionGroup, List<UserAnswer>> answersByQuestionGroup = userAnswers.stream().collect(
                        Collectors.groupingBy(userAnswer -> userAnswer.getQuestion().getQuestionGroup(),
                                LinkedHashMap::new, Collectors.toList()));
                List<LearnerTestQuestionGroupResponse> groupResponses = answersByQuestionGroup.entrySet().stream().sorted(
                                Comparator.comparing(questionGroupListEntry -> questionGroupListEntry.getKey().getPosition()))
                        .map(group -> {
                            QuestionGroup questionGroup = group.getKey();
                            List<UserAnswer> userAnswerList = group.getValue();
                            List<LearnerAnswerResponse> answerResponses = userAnswerList.stream().sorted(Comparator.comparing(userAnswer -> userAnswer.getQuestion().getPosition()))
                                    .map(userAnswerMapper::toLearnerAnswerResponse).toList();

                            List<Object> questionsAsObject = new ArrayList<>(answerResponses);
                            LearnerTestQuestionGroupResponse groupResponse = questionGroupMapper.toLearnerTestQuestionGroupResponse(questionGroup);
                            groupResponse.setQuestions(new ArrayList<>(questionsAsObject));
                            return groupResponse;
                        })
                        .toList();
                LearnerTestPartResponse partResponse = partMapper.toLearnerTestPartResponse(part);
                partResponse.setQuestionGroups(new ArrayList<>(groupResponses));
                return partResponse;
            }).sorted(Comparator.comparing(LearnerTestPartResponse::getPartName)).toList();

            testPartsResponse.setPartResponses(partResponses);
        }

        return RetestResultOverallResponse.builder()
                .learnerTestPartsResponse(testPartsResponse)
                .totalQuestions(totalQuestions)
                .correctAnswers(correctAnswers)
                .timeSpent(timeSpent).build();
    }

    @Override
    @Transactional(readOnly = true)
    public LearnerTestPartsResponse getQuestionsAndCorrectAnswersWrongAnswer(Long userTestId, String email) {
        UserTest userTest = userTestRepository.findUserTestByIdWithWrongAnswer(userTestId, email).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User test"));
        LearnerTestPartsResponse learnerTestPartsResponse = testMapper.toLearnerTestPartsResponse(userTest.getTest());

        if (userTest.getUserAnswers() == null || userTest.getUserAnswers().isEmpty())
            learnerTestPartsResponse.setPartResponses(Collections.emptyList());
        else {
            Map<Part, List<UserAnswer>> answersByPart = groupWrongAnswersByPart(userTest.getUserAnswers());
            List<LearnerTestPartResponse> partResponses = answersByPart.entrySet().stream().map(entry -> {
                Part part = entry.getKey();
                List<UserAnswer> userAnswers = entry.getValue();
                Map<QuestionGroup, List<UserAnswer>> answersByQuestionGroup = userAnswers.stream().collect(
                        Collectors.groupingBy(userAnswer -> userAnswer.getQuestion().getQuestionGroup(),
                                LinkedHashMap::new, Collectors.toList()));
                List<LearnerTestQuestionGroupResponse> groupResponses = answersByQuestionGroup.entrySet().stream().sorted(
                                Comparator.comparing(questionGroupListEntry -> questionGroupListEntry.getKey().getPosition()))
                        .map(group -> {
                            QuestionGroup questionGroup = group.getKey();
                            List<UserAnswer> userAnswerList = group.getValue();
                            List<RedoWrongQuestionResponse> questionResponses = userAnswerList.stream().sorted(Comparator.comparing(question -> question.getQuestion().getPosition()))
                                    .map(question -> questionMapper.toRedoWrongQuestionResponse(question.getQuestion())).toList();

                            LearnerTestQuestionGroupResponse groupResponse = questionGroupMapper.toLearnerTestQuestionGroupResponse(questionGroup);
                            groupResponse.setQuestions(new ArrayList<>(questionResponses));
                            return groupResponse;
                        })
                        .toList();
                LearnerTestPartResponse partResponse = partMapper.toLearnerTestPartResponse(part);
                partResponse.setQuestionGroups(new ArrayList<>(groupResponses));
                return partResponse;
            }).sorted(Comparator.comparing(LearnerTestPartResponse::getPartName)).toList();

            learnerTestPartsResponse.setPartResponses(partResponses);
        }
        return learnerTestPartsResponse;
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

    private Map<String, List<UserAnswerGroupedByTagResponse>> groupUserAnswersByPartAndTag(UserTest userTest) {
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

    private List<UserAnswerGroupedByTagResponse> groupByTag(List<UserAnswer> answersInPart) {
        if (answersInPart == null || answersInPart.isEmpty())
            return List.of();

        Map<String, List<UserAnswer>> answersByTag = answersInPart.stream().flatMap(ua -> {
            if (ua.getQuestion() == null || ua.getQuestion().getTags() == null)
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

    private void calculate(UserTest userTest, List<UserAnswerRequest> answerRequests) {
        boolean isExamMode = userTest.getParts() == null || userTest.getParts().isEmpty();
        ScoreAccumulator accumulator = new ScoreAccumulator();

        if (isExamMode) {
            Map<Long, List<UserAnswerRequest>> groupedByGroupId = answerRequests.stream().collect(
                    Collectors.groupingBy(UserAnswerRequest::getQuestionGroupId));
            Set<Long> questionGroupIds = groupedByGroupId.keySet();
            Map<Long, QuestionGroup> groupMap = questionGroupService.findAllByIdsWithQuestions(questionGroupIds).stream()
                    .collect(Collectors.toMap(QuestionGroup::getId, g -> g));

            for (Map.Entry<Long, List<UserAnswerRequest>> e : groupedByGroupId.entrySet()) {
                QuestionGroup questionGroup = groupMap.get(e.getKey());
                if (questionGroup == null)
                    throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question group");
                boolean isListening = questionGroupService.isListeningPart(questionGroup.getPart());
                Map<Long, Question> questionMap = questionGroup.getQuestions().stream().collect(Collectors.toMap(Question::getId, q -> q));

                for (UserAnswerRequest userAnswerRequest : e.getValue()) {
                    Question question = questionMap.get(userAnswerRequest.getQuestionId());
                    if (question == null)
                        throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question");
                    calculatorScore(userAnswerRequest, question, accumulator, isListening, userTest);
                }
            }
            int listenScore = estimatedListeningScoreMap.getOrDefault(accumulator.getListeningCorrectAnswers(), 0);
            int readingScore = estimatedReadingScoreMap.getOrDefault(accumulator.getReadingCorrectAnswers(), 0);
            userTest.setListeningScore(listenScore);
            userTest.setReadingScore(readingScore);
            userTest.setTotalScore(listenScore + readingScore);
        } else {
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
                                 boolean isListening, UserTest userTest) {
        boolean correct = userAnswerRequest.getAnswer() != null && userAnswerRequest.getAnswer().equals(question.getCorrectOption());
        if (correct) {
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

    private Map<Part, List<UserAnswer>> groupWrongAnswersByPart(List<UserAnswer> userAnswers) {
        return userAnswers.stream()
                .filter(userAnswer -> userAnswer.getQuestion() != null && userAnswer.getQuestion().getQuestionGroup() != null
                        && userAnswer.getQuestion().getQuestionGroup().getPart() != null)
                .collect(Collectors.groupingBy(userAnswer -> userAnswer.getQuestion().getQuestionGroup().getPart(),
                        LinkedHashMap::new, Collectors.toList()));
    }
}
