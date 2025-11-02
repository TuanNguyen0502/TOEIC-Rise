package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.responses.TestResultOverallResponse;
import com.hcmute.fit.toeicrise.models.entities.UserTest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserTestMapper {
    default TestResultOverallResponse toTestResultResponse(UserTest userTest) {
        return TestResultOverallResponse.builder()
                .userTestId(userTest.getId())
                .totalQuestions(userTest.getTotalQuestions())
                .correctAnswers(userTest.getCorrectAnswers())
                .score(userTest.getTotalScore() != null ? userTest.getTotalScore() : 0)
                .timeSpent(userTest.getTimeSpent())
                .build();
    }
}
