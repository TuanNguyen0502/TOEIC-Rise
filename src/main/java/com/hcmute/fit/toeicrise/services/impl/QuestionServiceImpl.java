package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionRequest;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestQuestionResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.entities.Tag;
import com.hcmute.fit.toeicrise.dtos.responses.test.QuestionResponse;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.QuestionMapper;
import com.hcmute.fit.toeicrise.repositories.QuestionRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionService;
import com.hcmute.fit.toeicrise.services.interfaces.ITagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements IQuestionService {
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
        List<Tag> tags = tagService.getTagsFromString(questionRequest.getTags());
        question = questionMapper.toEntity(questionRequest, question);
        question.setTags(tags);
        questionRepository.save(question);
    }

    @Override
    public QuestionResponse getQuestionById(Long questionId) {
        return questionMapper.toQuestionResponse(questionRepository.findById(questionId).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question")));
    }

    @Override
    public List<Question> getQuestionEntitiesByIds(List<Long> questionIds) {
        return questionRepository.findAllById(questionIds);
    }

    @Override
    public List<LearnerTestQuestionResponse> getLearnerTestQuestionsByQuestionGroupId(Long questionGroupId) {
        return questionRepository.findAllByQuestionGroup_Id(questionGroupId)
                .stream()
                .map(questionMapper::toLearnerTestQuestionResponse)
                .toList();
    }
}
