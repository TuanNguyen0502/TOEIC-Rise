package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.QuestionGroupResponse;

import java.util.List;

public interface IQuestionGroupService {
    List<QuestionGroupResponse> getQuestionGroupsByTestId(Long testId);
}
