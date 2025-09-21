package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.dtos.responses.QuestionResponse;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.Tag;
import org.mapstruct.*;

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

    /**
     * Maps a Set of Tag entities to a Set of tag names (String)
     */
    default Set<String> map(Set<Tag> tags) {
        if (tags == null) {
            return Set.of();
        }
        return tags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
    }
}
