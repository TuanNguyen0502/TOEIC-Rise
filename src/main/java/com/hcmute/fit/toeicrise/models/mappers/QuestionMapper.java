package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.dtos.responses.QuestionResponse;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.Tag;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    @Mapping(source = "questionGroup.position", target = "questionGroupPosition")
    @Mapping(source = "questionGroup.part.name", target = "part")
    @Mapping(source = "createdAt", target = "createdAt", dateFormat = Constant.DATE_TIME_PATTERN)
    @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = Constant.DATE_TIME_PATTERN)
    @Mapping(source = "tags", target = "tags")
    QuestionResponse toQuestionResponse(Question question);

    default List<String> map(Set<Tag> tags) {
        if (tags == null) {
            return List.of();
        }
        return tags.stream()
                .map(Tag::getName)
                .distinct()
                .collect(Collectors.toList());
    }
}
