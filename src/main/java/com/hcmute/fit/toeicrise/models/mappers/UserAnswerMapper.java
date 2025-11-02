package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.responses.UserAnswerDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.UserAnswerOverallResponse;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.entities.Tag;
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

    default UserAnswerDetailResponse toUserAnswerDetailResponse(UserAnswer userAnswer,
                                                                QuestionGroup questionGroup) {
        Question question = userAnswer.getQuestion();
        return UserAnswerDetailResponse.builder()
                .position(question.getPosition())
                .tags(question.getTags().stream().map(Tag::getName).toList())
                .audioUrl(questionGroup.getAudioUrl())
                .imageUrl(questionGroup.getImageUrl())
                .passage(questionGroup.getPassage())
                .transcript(questionGroup.getTranscript())
                .questionContent(question.getContent())
                .options(question.getOptions())
                .correctOption(question.getCorrectOption())
                .explanation(question.getExplanation())
                .build();
    }
}
