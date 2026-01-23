package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.responses.tag.TagDashboardResponse;
import com.hcmute.fit.toeicrise.dtos.responses.tag.TagResponse;
import com.hcmute.fit.toeicrise.dtos.responses.minitest.TagByPartResponse;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.Tag;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagResponse toTagResponse(Tag tag);

    default TagByPartResponse mapToTagByPartResponse(Tag tag) {
        return TagByPartResponse.builder()
                .tagId(tag.getId())
                .tagName(tag.getName())
                .build();
    }

    default TagDashboardResponse mapToTagDashboardResponse(Tag tag) {
        return TagDashboardResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .questionCount(tag.getQuestions().stream().map(Question::getId).toList().size())
                .userAnswerCount(0)
                .correctRate(0.0f)
                .build();
    }
}
