package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.dtos.responses.*;
import com.hcmute.fit.toeicrise.models.entities.Test;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TestMapper {
    @Mapping(source = "createdAt", target = "createdAt", dateFormat = Constant.DATE_TIME_PATTERN)
    @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = Constant.DATE_TIME_PATTERN)
    TestResponse toResponse(Test test);

    @Mapping(source = "createdAt", target = "createdAt", dateFormat = Constant.DATE_TIME_PATTERN)
    @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = Constant.DATE_TIME_PATTERN)
    TestDetailResponse toDetailResponse(Test test, @Context List<PartResponse> partResponses);

    @AfterMapping
    default void setPartResponses(@MappingTarget TestDetailResponse dto,
                                @Context List<PartResponse> partResponses) {
        dto.setPartResponses(partResponses);
    }
}