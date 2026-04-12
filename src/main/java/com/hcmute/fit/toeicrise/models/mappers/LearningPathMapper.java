package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LearningPathCreateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.learningpath.LearningPathDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learningpath.LearningPathSummaryResponse;
import com.hcmute.fit.toeicrise.models.entities.LearningPath;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface LearningPathMapper {
    LearningPath toEntity(LearningPathCreateRequest request);

    @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = Constant.DATE_TIME_PATTERN)
    LearningPathSummaryResponse toSummaryResponse(LearningPath learningPath);

    LearningPathDetailResponse toLearningPathDetailResponse(LearningPath learningPath);

    @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = Constant.DATE_TIME_PATTERN)
    default LearningPathSummaryResponse toSummaryResponse(LearningPath learningPath, Long lessonCount) {
        LearningPathSummaryResponse response = toSummaryResponse(learningPath);
        response.setLessonCount(lessonCount == null ? 0 : lessonCount.intValue());
        return response;
    }
}
