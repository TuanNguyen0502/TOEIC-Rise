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

public interface IQuestionService {
    void createQuestion(QuestionExcelRequest request, QuestionGroup questionGroup, List<Tag> tags);

    List<QuestionResponse> getQuestionsByQuestionGroupId(Long questionGroupId);

    @Transactional
    void updateQuestion(QuestionRequest questionRequest);

    QuestionResponse getQuestionById(Long questionId);

    List<Question> getQuestionEntitiesByIds(List<Long> questionIds);

    Optional<Question> findById(Long aLong);

    void updateQuestionWithEntity(Question question, QuestionRequest request);
}