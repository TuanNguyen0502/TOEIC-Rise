package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.dtos.responses.learningpath.LessonWithProgressResponse;
import com.hcmute.fit.toeicrise.models.entities.UserLessonProgress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserLessonProgressMapper {
    @Mapping(source = "updatedAt", target = "progressUpdatedAt", dateFormat = Constant.DATE_TIME_PATTERN)
    @Mapping(source = "createdAt", target = "createdAt", dateFormat = Constant.DATE_TIME_PATTERN)
    LessonWithProgressResponse toLessonWithProgressResponse(UserLessonProgress user);
}
