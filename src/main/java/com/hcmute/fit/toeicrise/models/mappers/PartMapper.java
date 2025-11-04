package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.responses.PartResponse;
import com.hcmute.fit.toeicrise.dtos.responses.QuestionGroupResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerPartResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestPartResponse;
import com.hcmute.fit.toeicrise.models.entities.Part;
import org.mapstruct.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface PartMapper {
    PartResponse toPartResponse(Part part, @Context List<QuestionGroupResponse> questionGroups);
    @Mapping(source = "name", target = "partName")
    LearnerTestPartResponse toLearnerTestPartResponse(Part part);

    @AfterMapping
    default void setQuestionGroups(@MappingTarget PartResponse dto,
                                   @Context List<QuestionGroupResponse> questionGroups) {
        dto.setQuestionGroups(questionGroups);
    }

    default LearnerPartResponse mapToLearnerPartResponse(Object[] objects){
        String tagNamesStr = (String) objects[5];
        List<String> tagNames = Optional.ofNullable(tagNamesStr)
                .filter(s -> !s.isEmpty())
                .map(s -> Arrays.asList(s.split(";\\s*")))
                .orElse(Collections.emptyList());
        return LearnerPartResponse.builder()
                .partId(((Number) objects[4]).longValue())
                .partName((String) objects[3])
                .tagNames(tagNames)
                .build();
    }
}
