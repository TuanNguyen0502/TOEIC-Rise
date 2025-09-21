package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.QuestionResponse;

import java.util.List;

public interface IQuestionService {
    List<QuestionResponse> getQuestionsByQuestionGroupId(Long questionGroupId);
}
