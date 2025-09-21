package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.QuestionResponse;
import org.springframework.data.domain.Page;

public interface IQuestionService {
    int countQuestionsByQuestionGroupId(Long questionGroupId);

    Page<QuestionResponse> getQuestionsByTestId(Long testId, String part, int page, int size, String sortBy, String direction);
}
