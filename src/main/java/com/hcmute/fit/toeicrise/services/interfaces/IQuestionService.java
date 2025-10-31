package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.requests.QuestionRequest;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.entities.Tag;
import com.hcmute.fit.toeicrise.dtos.responses.QuestionResponse;

import java.util.List;

public interface IQuestionService {
    Question createQuestion(QuestionExcelRequest request, QuestionGroup questionGroup, List<Tag> tags);
    List<QuestionResponse> getQuestionsByQuestionGroupId(Long questionGroupId);
    void updateQuestion(QuestionRequest questionRequest);
    QuestionResponse getQuestionById(Long questionId);
}