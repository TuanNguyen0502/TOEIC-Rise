package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.responses.test.PartResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.QuestionGroupResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerPartResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestPartResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.speaking.SpeakingPartResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.speaking.SpeakingQuestionGroupResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.writing.WritingPartResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.writing.WritingQuestionGroupResponse;
import com.hcmute.fit.toeicrise.models.entities.Part;
import org.mapstruct.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface PartMapper {
    @Mapping(target = "questionGroups", ignore = true)
    PartResponse toPartResponse(Part part, @Context List<QuestionGroupResponse> questionGroups);

    @Mapping(source = "name", target = "partName")
    @Mapping(target = "questionGroups", ignore = true)
    LearnerTestPartResponse toLearnerTestPartResponse(Part part);

    @AfterMapping
    default void setQuestionGroups(@MappingTarget PartResponse dto,
                                   @Context List<QuestionGroupResponse> questionGroups) {
        dto.setQuestionGroups(questionGroups);
    }

    default LearnerPartResponse mapToLearnerPartResponse(Object[] objects) {
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

    default SpeakingPartResponse toSpeakingPartResponse(Part part, List<SpeakingQuestionGroupResponse> questionGroupResponses) {
        return SpeakingPartResponse.builder()
                .id(part.getId())
                .name(part.getName())
                .questionGroups(questionGroupResponses)
                .build();
    }

    default WritingPartResponse toWritingPartResponse(Part part, List<WritingQuestionGroupResponse> questionGroupResponses) {
        return WritingPartResponse.builder()
                .id(part.getId())
                .name(part.getName())
                .questionGroups(questionGroupResponses)
                .build();
    }
}
