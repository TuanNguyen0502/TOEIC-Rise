package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.responses.TestSetResponse;
import com.hcmute.fit.toeicrise.models.entities.TestSet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TestSetMapper {
    TestSetResponse toTestSetResponse(TestSet testSet);
}
