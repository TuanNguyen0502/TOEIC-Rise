package com.hcmute.fit.toeicrise.services.interfaces;

import java.util.List;

public interface IQuestionTagService {
    List<String> getTagsByQuestionId(Long questionId);
}
