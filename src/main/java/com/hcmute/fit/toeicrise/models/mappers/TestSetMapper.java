package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.testset.TestSetDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.testset.TestSetResponse;
import com.hcmute.fit.toeicrise.models.entities.TestSet;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TestSetMapper {
    @Mapping(source = "createdAt", target = "createdAt", dateFormat = Constant.DATE_TIME_PATTERN)
    @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = Constant.DATE_TIME_PATTERN)
    TestSetResponse toTestSetResponse(TestSet testSet);

    @Mapping(source = "createdAt", target = "createdAt", dateFormat = Constant.DATE_TIME_PATTERN)
    @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = Constant.DATE_TIME_PATTERN)
    TestSetDetailResponse toTestSetDetailResponse(TestSet testSet, @Context PageResponse testResponses);

    @AfterMapping
    default void setTestResponses(@MappingTarget TestSetDetailResponse dto,
                                  @Context PageResponse testResponses) {
        dto.setTestResponses(testResponses);
    }
}