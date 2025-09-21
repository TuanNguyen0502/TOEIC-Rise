package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.responses.PartResponse;
import com.hcmute.fit.toeicrise.dtos.responses.QuestionGroupResponse;
import com.hcmute.fit.toeicrise.models.entities.Part;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PartMapper {
    PartResponse toPartResponse(Part part, @Context List<QuestionGroupResponse> questionGroups);

    @AfterMapping
    default void setQuestionGroups(@MappingTarget PartResponse dto,
                                   @Context List<QuestionGroupResponse> questionGroups) {
        dto.setQuestionGroups(questionGroups);
    }
}
