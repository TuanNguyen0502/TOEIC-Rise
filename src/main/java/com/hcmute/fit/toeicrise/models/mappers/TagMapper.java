package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.responses.tag.TagDashboardResponse;
import com.hcmute.fit.toeicrise.dtos.responses.tag.TagResponse;
import com.hcmute.fit.toeicrise.dtos.responses.minitest.TagByPartResponse;
import com.hcmute.fit.toeicrise.dtos.responses.tag.TagStatisticsProjection;
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

    default TagDashboardResponse mapToTagDashboardResponse(TagStatisticsProjection tagStatistics) {
        return TagDashboardResponse.builder()
                .id(tagStatistics.getId())
                .name(tagStatistics.getName())
                .questionCount(tagStatistics.getTotalQuestions())
                .userAnswerCount(tagStatistics.getTotalAnswers())
                .correctRate(tagStatistics.getCorrectionRate())
                .build();
    }
}
