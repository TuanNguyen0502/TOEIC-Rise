package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.dtos.responses.QuestionGroupResponse;
import com.hcmute.fit.toeicrise.dtos.responses.QuestionResponse;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionGroupMapper {
    @Mapping(source = "createdAt", target = "createdAt", dateFormat = Constant.DATE_TIME_PATTERN)
    @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = Constant.DATE_TIME_PATTERN)
    QuestionGroupResponse toResponse(QuestionGroup questionGroup, @Context List<QuestionResponse> questions);

    @AfterMapping
    default void linkQuestions(@MappingTarget QuestionGroupResponse questionGroupResponse, @Context List<QuestionResponse> questions) {
        questionGroupResponse.setQuestions(questions);
    }
}
