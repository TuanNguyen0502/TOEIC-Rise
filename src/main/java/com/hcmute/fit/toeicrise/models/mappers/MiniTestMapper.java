package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.responses.minitest.MiniTestAnswerQuestionResponse;
import com.hcmute.fit.toeicrise.models.entities.Question;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MiniTestMapper {
    MiniTestAnswerQuestionResponse toMiniTestAnswerQuestionResponse(Question question);
}
