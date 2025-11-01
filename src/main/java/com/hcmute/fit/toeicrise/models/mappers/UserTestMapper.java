package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.responses.TestResultResponse;
import com.hcmute.fit.toeicrise.models.entities.UserTest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserTestMapper {
    default TestResultResponse toTestResultResponse(UserTest userTest) {
        return TestResultResponse.builder()
                .userTestId(userTest.getId())
                .totalQuestions(userTest.getTotalQuestions())
                .correctAnswers(userTest.getCorrectAnswers())
                .timeSpent(userTest.getTimeSpent())
                .build();
    }
}
