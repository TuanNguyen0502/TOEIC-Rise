package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.utils.ShuffleUtil;
import com.hcmute.fit.toeicrise.dtos.requests.minitest.MiniQuestionGroupRequest;
import com.hcmute.fit.toeicrise.dtos.requests.minitest.MiniTestRequest;
import com.hcmute.fit.toeicrise.dtos.requests.minitest.UserAnswerMiniTestRequest;
import com.hcmute.fit.toeicrise.dtos.responses.minitest.*;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.QuestionGroupMapper;
import com.hcmute.fit.toeicrise.models.mappers.QuestionMapper;
import com.hcmute.fit.toeicrise.services.interfaces.IMiniTestService;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionService;
import com.hcmute.fit.toeicrise.services.interfaces.ITagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MiniTestServiceImpl implements IMiniTestService {
    private final IQuestionService questionService;
    private final QuestionMapper questionMapper;
    private final QuestionGroupMapper questionGroupMapper;
    private final ITagService tagService;

    @Override
    public MiniTestOverallResponse getMiniTestOverallResponse(MiniTestRequest request) {
        List<Long> questionIds = request.getQuestionGroups().stream()
                .flatMap(group -> group.getUserAnswerRequests().stream())
                .map(UserAnswerMiniTestRequest::getQuestionId)
                .filter(Objects::nonNull)
                .distinct().toList();

        List<Question> questions = questionService.getQuestionsWithGroupsByIds(questionIds);
        questionService.validateQuestion(questionIds, questions);
        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));
        MiniTestOverallResponse miniTestOverallResponse = calculatorAnswerMiniTest(request, questionMap);
        miniTestOverallResponse.setTotalQuestions(questions.size());
        return miniTestOverallResponse;
    }

    private MiniTestOverallResponse calculatorAnswerMiniTest(MiniTestRequest miniTestRequest, Map<Long, Question> questionMap) {
        int correctAnswers = 0;
        Map<QuestionGroup, List<MiniTestAnswerQuestionResponse>> miniTestAnswerQuestionResponses = new LinkedHashMap<>();
        int groupPosition = 1;
        long globalQuestionPosition = 1;

        for (MiniQuestionGroupRequest questionGroupRequest : miniTestRequest.getQuestionGroups()) {
            if (questionGroupRequest.getQuestionGroupId() == null)
                throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question group");

            for (UserAnswerMiniTestRequest userAnswerRequest : questionGroupRequest.getUserAnswerRequests()) {
                if (userAnswerRequest.getQuestionId() == null)
                    throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question");
                Question question = questionMap.get(userAnswerRequest.getQuestionId());
                if (question == null) throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question");
                if (!question.getQuestionGroup().getId().equals(questionGroupRequest.getQuestionGroupId()))
                    throw new AppException(ErrorCode.VALIDATION_ERROR);

                boolean isCorrect = userAnswerRequest.getAnswer() != null && question.getCorrectOption() != null
                        && question.getCorrectOption().equals(userAnswerRequest.getAnswer());
                if (isCorrect) correctAnswers++;

                MiniTestAnswerQuestionResponse miniTestAnswerQuestionResponse = questionMapper.toMiniTestAnswerQuestionResponse(question);
                miniTestAnswerQuestionResponse.setUserAnswer(userAnswerRequest.getAnswer());
                miniTestAnswerQuestionResponse.setIsCorrect(isCorrect);

                miniTestAnswerQuestionResponses.computeIfAbsent(question.getQuestionGroup(), _ -> new ArrayList<>()).add(miniTestAnswerQuestionResponse);
            }
        }
        List<MiniTestQuestionGroupAnswerResponse> groupResponses = new ArrayList<>();
        for (Map.Entry<QuestionGroup, List<MiniTestAnswerQuestionResponse>> entry : miniTestAnswerQuestionResponses.entrySet()) {
            MiniTestQuestionGroupAnswerResponse miniTestQuestionGroupResponse = questionGroupMapper.toMiniTestQuestionGroupAnswerResponse(entry.getKey());
            miniTestQuestionGroupResponse.setIndex(groupPosition++);
            for (MiniTestAnswerQuestionResponse miniTestAnswerQuestionResponse : entry.getValue()) {
                miniTestAnswerQuestionResponse.setIndex(globalQuestionPosition++);
            }
            miniTestQuestionGroupResponse.setQuestions(entry.getValue());
            groupResponses.add(miniTestQuestionGroupResponse);
        }
        return MiniTestOverallResponse.builder()
                .correctAnswers(correctAnswers)
                .questionGroups(groupResponses).build();
    }

    @Override
    public MiniTestResponse getLearnerTestQuestionGroupResponsesByTags(Long partId, Set<Long> tagIds, int numberQuestion) {
        tagService.checkExistsIds(tagIds);

        Map<QuestionGroup, List<Question>> groupEntities = getAllQuestionGroup(partId, tagIds, numberQuestion);
        groupEntities.values().forEach(questions -> questions.sort(Comparator.comparing(Question::getPosition)));
        List<MiniTestQuestionGroupResponse> miniTestQuestionGroupResponses = new ArrayList<>();
        int groupPosition = 1;
        long globalQuestionPosition = 1;

        for (Map.Entry<QuestionGroup, List<Question>> entry : groupEntities.entrySet()) {
            MiniTestQuestionGroupResponse groupResponse = questionGroupMapper.toMiniTestQuestionGroupResponse(entry.getKey());
            groupResponse.setIndex(groupPosition++);
            List<MiniTestQuestionResponse> questionResponses = new ArrayList<>();
            for (Question question : entry.getValue()) {
                MiniTestQuestionResponse questionResponse = questionMapper.toMiniTestQuestionResponse(question);
                questionResponse.setIndex(globalQuestionPosition++);
                questionResponses.add(questionResponse);
            }
            groupResponse.setQuestions(questionResponses);
            miniTestQuestionGroupResponses.add(groupResponse);
        }
        return MiniTestResponse.builder()
                .questionGroups(miniTestQuestionGroupResponses)
                .totalQuestions(globalQuestionPosition - 1).build();
    }

    private Map<QuestionGroup, List<Question>> getAllQuestionGroup(Long partId, Set<Long> tagIds, int numberQuestion) {
        List<Question> allQuestions = questionService.getAllQuestionsByPartAndTags(tagIds, partId);
        ShuffleUtil.shuffle(allQuestions);
        Map<Long, List<Question>> questionsByTag = new LinkedHashMap<>();

        for (Long tagId : tagIds) {
            List<Question> tagQuestions = allQuestions.stream().filter(
                    question -> question.getTags().stream().anyMatch(
                            tag -> tag.getId().equals(tagId))).collect(Collectors.toList());
            questionsByTag.put(tagId, tagQuestions);
        }
        List<Question> selectedQuestions = new ArrayList<>();
        Set<Long> usedQuestionIds = new HashSet<>();
        Map<Long, Integer> tagIndices = new HashMap<>();
        tagIds.forEach(tagId -> tagIndices.put(tagId, 0));

        while (selectedQuestions.size() < numberQuestion) {
            boolean addedInThisRound = false;

            for (Long tagId : tagIds) {
                if (selectedQuestions.size() >= numberQuestion)
                    break;
                List<Question> tagQuestions = questionsByTag.get(tagId);
                if (tagQuestions == null || tagQuestions.isEmpty())
                    throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question");
                Integer currentIndex = tagIndices.get(tagId);

                while (currentIndex < tagQuestions.size()) {
                    Question tagQuestion = tagQuestions.get(currentIndex);
                    currentIndex++;
                    if (!usedQuestionIds.contains(tagQuestion.getId())) {
                        selectedQuestions.add(tagQuestion);
                        usedQuestionIds.add(tagQuestion.getId());
                        addedInThisRound = true;
                        break;
                    }
                }
                tagIndices.put(tagId, currentIndex);
            }
            if (!addedInThisRound)
                break;
        }
        return selectedQuestions.stream().collect(Collectors.groupingBy(Question::getQuestionGroup, LinkedHashMap::new, Collectors.toList()));
    }

}
