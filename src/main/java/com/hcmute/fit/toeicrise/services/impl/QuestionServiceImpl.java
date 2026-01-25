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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements IQuestionService {
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;
    private final ITagService tagService;

    @Override
    public void createQuestion(QuestionExcelRequest request, QuestionGroup questionGroup, List<Tag> tags) {
        Question question = questionMapper.toEntity(request, questionGroup);
        questionRepository.save(question);
        if (tags != null && !tags.isEmpty()) {
            question.setTags(new ArrayList<>(tags));
            questionRepository.save(question);
        }
    }

    @Override
    public List<QuestionResponse> getQuestionsByQuestionGroupId(Long questionGroupId) {
        return questionRepository.findAllByQuestionGroup_Id(questionGroupId)
                .stream()
                .map(questionMapper::toQuestionResponse)
                .toList();
    }

    @Transactional
    @Override
    public void updateQuestion(QuestionRequest questionRequest) {
        Question question = questionRepository.findById(questionRequest.getId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question"));
        updateQuestionWithEntity(question, questionRequest);
    }

    @Override
    public QuestionResponse getQuestionById(Long questionId) {
        return questionMapper.toQuestionResponse(questionRepository.findById(questionId).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question")));
    }

    @Override
    public Optional<Question> findById(Long aLong) {
        return questionRepository.findById(aLong);
    }

    @Override
    public void updateQuestionWithEntity(Question question, QuestionRequest request) {
        List<Tag> tags = tagService.parseTagsAllowCreate(request.getTags());
        question = questionMapper.toEntity(request, question);
        question.setTags(tags);
        questionRepository.save(question);

        // Change test status to PENDING
        changeTestStatus(question);
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
        if (questions.size() != questionIds.size()) {
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

    @Async
    public void changeTestStatus(Question question) {
        Test test = question.getQuestionGroup().getTest();
        if (test.getStatus() != ETestStatus.PENDING) {
            test.setStatus(ETestStatus.PENDING);
            testRepository.save(test);
        }
    }
}
