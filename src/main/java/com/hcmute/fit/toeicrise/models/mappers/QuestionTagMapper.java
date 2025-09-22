package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.QuestionTag;
import com.hcmute.fit.toeicrise.models.entities.QuestionTagId;
import com.hcmute.fit.toeicrise.models.entities.Tag;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuestionTagMapper {
    default QuestionTag toEntity(QuestionTagId questionTagId, Question question, Tag tag) {
        QuestionTag questionTag = new QuestionTag();
        questionTag.setId(questionTagId);
        questionTag.setQuestion(question);
        questionTag.setTag(tag);
        return questionTag;
    }
}