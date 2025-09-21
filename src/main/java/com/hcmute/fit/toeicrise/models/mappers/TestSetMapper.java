package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.responses.TestResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestSetDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestSetResponse;
import com.hcmute.fit.toeicrise.models.entities.TestSet;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface TestSetMapper {
    TestSetResponse toTestSetResponse(TestSet testSet);

    TestSetDetailResponse toTestSetDetailResponse(TestSet testSet, Page<TestResponse> testResponsePage);
}
