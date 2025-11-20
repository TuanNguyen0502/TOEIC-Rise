package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionRequest;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestQuestionResponse;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.entities.Tag;
import com.hcmute.fit.toeicrise.dtos.responses.test.QuestionResponse;

import java.util.List;

public interface IQuestionService {
    void createQuestion(QuestionExcelRequest request, QuestionGroup questionGroup, List<Tag> tags);

    List<QuestionResponse> getQuestionsByQuestionGroupId(Long questionGroupId);

    void updateQuestion(QuestionRequest questionRequest);

    QuestionResponse getQuestionById(Long questionId);

    List<Question> getQuestionEntitiesByIds(List<Long> questionIds);

    List<LearnerTestQuestionResponse> getLearnerTestQuestionsByQuestionGroupId(Long questionGroupId);
}