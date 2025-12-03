package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.responses.TagResponse;
import com.hcmute.fit.toeicrise.dtos.responses.minitest.TagByPartResponse;
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
}
