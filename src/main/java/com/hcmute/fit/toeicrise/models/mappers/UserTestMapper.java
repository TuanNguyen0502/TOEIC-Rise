package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.responses.TestResultOverallResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestResultResponse;
import com.hcmute.fit.toeicrise.models.entities.UserTest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserTestMapper {
    default TestResultOverallResponse toTestResultOverallResponse(UserTest userTest) {
        return TestResultOverallResponse.builder()
                .userTestId(userTest.getId())
                .totalQuestions(userTest.getTotalQuestions())
                .correctAnswers(userTest.getCorrectAnswers())
                .score(userTest.getTotalScore() != null ? userTest.getTotalScore() : 0)
                .timeSpent(userTest.getTimeSpent())
                .build();
    }

    default TestResultResponse toTestResultResponse(UserTest userTest) {
        TestResultResponse.TestResultResponseBuilder builder = TestResultResponse.builder()
                .testId(userTest.getTest().getId())
                .userTestId(userTest.getId())
                .testName(userTest.getTest().getName())
                .parts(userTest.getParts() != null ? userTest.getParts() : null)
                .totalQuestions(userTest.getTotalQuestions())
                .correctAnswers(userTest.getCorrectAnswers())
                .correctPercent(userTest.getCorrectPercent())
                .timeSpent(userTest.getTimeSpent());

        if (userTest.getTotalScore() != null) {
            builder.score(userTest.getTotalScore())
                    .listeningScore(userTest.getListeningScore())
                    .listeningCorrectAnswers(userTest.getListeningCorrectAnswers())
                    .readingScore(userTest.getReadingScore())
                    .readingCorrectAnswers(userTest.getReadingCorrectAnswers());
        }

        return builder.build();
    }
}
