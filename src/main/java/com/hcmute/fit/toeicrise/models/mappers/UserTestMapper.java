package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.responses.TestResultOverallResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestResultResponse;
import com.hcmute.fit.toeicrise.dtos.responses.UserAnswerGroupedByTagResponse;
import com.hcmute.fit.toeicrise.models.entities.UserTest;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Map;

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

    default TestResultResponse toTestResultResponse(UserTest userTest,
                                                    Map<String, List<UserAnswerGroupedByTagResponse>> userAnswersByPart) {
        TestResultResponse.TestResultResponseBuilder builder = TestResultResponse.builder()
                .testId(userTest.getTest().getId())
                .userTestId(userTest.getId())
                .testName(userTest.getTest().getName())
                .parts(userTest.getParts() != null ? userTest.getParts() : null)
                .totalQuestions(userTest.getTotalQuestions())
                .correctAnswers(userTest.getCorrectAnswers())
                .correctPercent(userTest.getCorrectPercent())
                .timeSpent(userTest.getTimeSpent())
                .userAnswersByPart(userAnswersByPart);

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
