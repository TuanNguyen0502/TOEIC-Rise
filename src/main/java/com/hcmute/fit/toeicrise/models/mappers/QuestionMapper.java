package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.dtos.responses.QuestionResponse;
import com.hcmute.fit.toeicrise.models.entities.Question;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    @Mapping(source = "questionGroup.position", target = "questionGroupPosition")
    @Mapping(source = "questionGroup.part.name", target = "part")
    @Mapping(source = "createdAt", target = "createdAt", dateFormat = Constant.DATE_TIME_PATTERN)
    @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = Constant.DATE_TIME_PATTERN)
    QuestionResponse toQuestionResponse(Question question, @Context List<String> tags);

    @AfterMapping
    default void setTags(@MappingTarget QuestionResponse dto, @Context List<String> tags) {
        dto.setTags(tags);
    }
}
