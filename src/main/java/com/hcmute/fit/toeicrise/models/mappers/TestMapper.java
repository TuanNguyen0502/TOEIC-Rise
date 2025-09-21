package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.dtos.responses.QuestionGroupResponse;
import com.hcmute.fit.toeicrise.dtos.responses.QuestionResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestResponse;
import com.hcmute.fit.toeicrise.models.entities.Test;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TestMapper {
    @Mapping(source = "createdAt", target = "createdAt", dateFormat = Constant.DATE_TIME_PATTERN)
    @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = Constant.DATE_TIME_PATTERN)
    TestResponse toResponse(Test test);

    @Mapping(source = "id", target = "testId")
    @Mapping(source = "name", target = "testName")
    @Mapping(source = "status", target = "testStatus")
    @Mapping(source = "createdAt", target = "createdAt", dateFormat = Constant.DATE_TIME_PATTERN)
    @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = Constant.DATE_TIME_PATTERN)
    @Mapping(source = "testSet.id", target = "testSetId")
    @Mapping(source = "testSet.name", target = "testSetName")
    TestDetailResponse toDetailResponse(Test test,
                                        @Context List<QuestionGroupResponse> questionGroups,
                                        @Context Page<QuestionResponse> questions
    );

    @AfterMapping
    default void setExtraFields(@MappingTarget TestDetailResponse dto,
                                @Context List<QuestionGroupResponse> questionGroups,
                                @Context Page<QuestionResponse> questions) {
        dto.setQuestionGroups(questionGroups);
        dto.setQuestions(questions);
    }
}