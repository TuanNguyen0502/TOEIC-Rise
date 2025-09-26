package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.dtos.responses.TestResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestSetDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestSetResponse;
import com.hcmute.fit.toeicrise.models.entities.TestSet;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface TestSetMapper {
    @Mapping(source = "createdAt", target = "createdAt", dateFormat = Constant.DATE_TIME_PATTERN)
    @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = Constant.DATE_TIME_PATTERN)
    TestSetResponse toTestSetResponse(TestSet testSet);

    @Mapping(source = "createdAt", target = "createdAt", dateFormat = Constant.DATE_TIME_PATTERN)
    @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = Constant.DATE_TIME_PATTERN)
    TestSetDetailResponse toTestSetDetailResponse(TestSet testSet, @Context Page<TestResponse> testResponses);

    @AfterMapping
    default void setTestResponses(@MappingTarget TestSetDetailResponse dto,
                                  @Context Page<TestResponse> testResponses) {
        dto.setTestResponses(testResponses);
    }
}