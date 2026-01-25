package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionRequest;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.entities.Tag;
import com.hcmute.fit.toeicrise.dtos.responses.test.QuestionResponse;
import com.hcmute.fit.toeicrise.models.entities.Test;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.QuestionMapper;
import com.hcmute.fit.toeicrise.repositories.QuestionRepository;
import com.hcmute.fit.toeicrise.repositories.TestRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionService;
import com.hcmute.fit.toeicrise.services.interfaces.ITagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl implements IQuestionService {
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;
    private final ITagService tagService;

    @Override
    public List<QuestionResponse> getQuestionsByQuestionGroupId(Long questionGroupId) {
        if (questionGroupId == null)
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question group");
        return questionRepository.findAllByQuestionGroup_Id(questionGroupId)
                .stream()
                .map(questionMapper::toQuestionResponse)
                .toList();
    }

    @Transactional
    @Override
    public void updateQuestion(QuestionRequest questionRequest) {
        if (questionRequest == null|| questionRequest.getId() == null)
            throw new AppException(ErrorCode.INVALID_REQUEST, "Question");

        Question question = findById(questionRequest.getId());
        updateQuestionWithEntity(question, questionRequest);
    }

    @Override
    public QuestionResponse getQuestionResponseById(Long questionId) {
        if (questionId == null)
            throw new AppException(ErrorCode.INVALID_REQUEST, "Question");
        return questionMapper.toQuestionResponse(findById(questionId));
    }

    @Override
    public List<Question> getQuestionEntitiesByIds(List<Long> questionIds) {
        if (questionIds == null||questionIds.isEmpty())
            throw new AppException(ErrorCode.INVALID_REQUEST, "Question");
        return questionRepository.findAllById(questionIds);
    }

    @Override
    public Question findById(Long aLong) {
        if (aLong == null)
            throw new AppException(ErrorCode.INVALID_REQUEST, "Question");
        return questionRepository.findById(aLong).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question"));
    }

    @Override
    public void updateQuestionWithEntity(Question question, QuestionRequest request) {
        if (request == null|| question == null||request.getId() == null || question.getId() == null)
            throw new AppException(ErrorCode.INVALID_REQUEST, "Question");

        List<Tag> tags = tagService.parseTagsAllowCreate(request.getTags());
        question = questionMapper.toEntity(request, question);
        question.setTags(tags);
        questionRepository.save(question);
        log.info("Question updated successfully with id: {}", question.getId());

        changeTestStatusToPending(question.getQuestionGroup().getTest());
    }

    @Override
    public List<Question> getAllQuestionsByPartAndTags(Set<Long> tagIds, Long partId) {
        return questionRepository.findAllByPartIdAndTag(tagIds, partId, ETestStatus.APPROVED);
    }

    @Override
    public List<Question> getQuestionsWithGroupsByIds(List<Long> questionIds) {
        if (questionIds == null || questionIds.isEmpty())
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question");
        return questionRepository.findAllByIdWithGroups(questionIds);
    }

    @Override
    public void validateQuestion(List<Long> questionIds, List<Question> questions) {
        if (questionIds == null || questionIds.isEmpty())
            throw new AppException(ErrorCode.INVALID_REQUEST, "Question");
        if (questions == null || questions.isEmpty())
            throw new AppException(ErrorCode.INVALID_REQUEST, "Question");
        if (questions.size() != questionIds.size()){
            Set<Long> foundIds = questions.stream().map(Question::getId)
                    .collect(Collectors.toSet());
            if (!foundIds.containsAll(questionIds))
                throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question");
        }
        List<Long> questionsWithoutGroup = questions.stream().filter(question -> question.getQuestionGroup() == null)
                .map(Question::getId).toList();
        if (!questionsWithoutGroup.isEmpty())
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question without Question group");
    }

    @Override
    public List<Question> findAllQuestionByIdWithTags(Set<Long> questionIds) {
        return questionRepository.findAllByIdWithTags(questionIds);
    }

    @Override
    public void createQuestionBatch(List<QuestionExcelRequest> questionExcelRequests, QuestionGroup questionGroup) {
        if (questionExcelRequests == null || questionExcelRequests.isEmpty())
            return;
        if (questionGroup == null)
            throw new AppException(ErrorCode.INVALID_REQUEST, "Question group");

        List<Question> questions = questionExcelRequests.stream().map(
                request -> questionMapper.toEntity(request, questionGroup)).toList();
        List<Question> savedQuestions = questionRepository.saveAll(questions);
        log.info("Saved questions: {}", savedQuestions);

        Map<Question, List<Tag>> questionListMap = new HashMap<>();
        for (int i = 0; i < savedQuestions.size(); i++) {
            Question question = savedQuestions.get(i);
            QuestionExcelRequest questionExcelRequest = questionExcelRequests.get(i);
            if (questionExcelRequest != null && questionExcelRequest.getTags() != null && !questionExcelRequest.getTags().isEmpty()) {
                List<Tag> tags = tagService.parseTagsAllowCreate(questionExcelRequest.getTags());
                questionListMap.put(question, tags);
            }
        }
        questionListMap.forEach(Question::setTags);
        if (!questionListMap.isEmpty())
            questionRepository.saveAll(questionListMap.keySet().stream().toList());
    }

    @Override
    public void changeTestStatusToPending(Test test) {
        if (test == null)
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test");
        if (test.getStatus() != ETestStatus.PENDING) {
            test.setStatus(ETestStatus.PENDING);
            testRepository.save(test);
            log.info("Test updated status successfully with id: {}", test.getId());
        }
    }
}
