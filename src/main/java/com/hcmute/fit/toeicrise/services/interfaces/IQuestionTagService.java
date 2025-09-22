package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.models.entities.Question;

public interface IQuestionTagService {
    void processQuestionTags(Question question, String tagsString);
}