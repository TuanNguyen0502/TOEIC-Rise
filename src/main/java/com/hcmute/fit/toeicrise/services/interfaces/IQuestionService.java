package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionRequest;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.entities.Tag;
import com.hcmute.fit.toeicrise.dtos.responses.test.QuestionResponse;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IQuestionService {
    void createQuestion(QuestionExcelRequest request, QuestionGroup questionGroup, List<Tag> tags);
    List<QuestionResponse> getQuestionsByQuestionGroupId(Long questionGroupId);
    @Transactional
    void updateQuestion(QuestionRequest questionRequest);
    QuestionResponse getQuestionById(Long questionId);
    List<Question> getQuestionEntitiesByIds(List<Long> questionIds);
    Optional<Question> findById(Long aLong);
    void updateQuestionWithEntity(Question question, QuestionRequest request);
    List<Question> getAllQuestionsByPartAndTags(Set<Long> tagIds, Long partId);
    List<Question> getQuestionsWithGroupsByIds(List<Long> questionIds);
    void validateQuestion(List<Long> questionIds, List<Question> questions);
    List<Question> findAllQuestionByIdWithTags(Set<Long> questionIds);
    void createQuestionBatch(List<QuestionExcelRequest> questions, QuestionGroup questionGroup);
}