package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.responses.UserAnswerOverallResponse;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.UserAnswer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserAnswerMapper {
    default UserAnswerOverallResponse toUserAnswerOverallResponse(UserAnswer userAnswer) {
        Question question = userAnswer.getQuestion();
        return UserAnswerOverallResponse.builder()
                .userAnswerId(userAnswer.getId())
                .position(question.getPosition())
                .correctAnswer(question.getCorrectOption())
                .userAnswer(userAnswer.getAnswer())
                .build();
    }
}
