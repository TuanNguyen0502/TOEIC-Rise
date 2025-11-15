package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.responses.UserAnswerDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.UserAnswerGroupedByTagResponse;
import com.hcmute.fit.toeicrise.dtos.responses.UserAnswerOverallResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerAnswerResponse;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.entities.Tag;
import com.hcmute.fit.toeicrise.models.entities.UserAnswer;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

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

    default UserAnswerGroupedByTagResponse.UserAnswerOverallResponse toUserAnswerGroupedByTagResponse(UserAnswer userAnswer) {
        Question question = userAnswer.getQuestion();
        return UserAnswerGroupedByTagResponse.UserAnswerOverallResponse.builder()
                .userAnswerId(userAnswer.getId())
                .position(question.getPosition())
                .isCorrect(question.getCorrectOption().equals(userAnswer.getAnswer()))
                .build();
    }

    default UserAnswerDetailResponse toUserAnswerDetailResponse(UserAnswer userAnswer,
                                                                QuestionGroup questionGroup) {
        Question question = userAnswer.getQuestion();
        List<String> options = new ArrayList<>();
        if (questionGroup.getPart().getName().contains("2")) {
            options.add(null);
            options.add(null);
            options.add(null);
        } else {
            options = question.getOptions();
        }
        return UserAnswerDetailResponse.builder()
                .userAnswer(userAnswer.getAnswer())
                .position(question.getPosition())
                .tags(question.getTags().stream().map(Tag::getName).toList())
                .audioUrl(questionGroup.getAudioUrl())
                .imageUrl(questionGroup.getImageUrl())
                .passage(questionGroup.getPassage())
                .transcript(questionGroup.getTranscript())
                .questionContent(question.getContent())
                .options(options)
                .correctOption(question.getCorrectOption())
                .explanation(question.getExplanation())
                .build();
    }

    default LearnerAnswerResponse toLearnerAnswerResponse(UserAnswer userAnswer){
        return LearnerAnswerResponse.builder()
                .id(userAnswer.getId())
                .position(userAnswer.getQuestion().getPosition().longValue())
                .content(userAnswer.getQuestion().getContent())
                .options(userAnswer.getQuestion().getOptions())
                .correctOption(userAnswer.getQuestion().getCorrectOption())
                .explanation(userAnswer.getQuestion().getExplanation())
                .userAnswer(userAnswer.getAnswer())
                .correctOption(userAnswer.getQuestion().getCorrectOption())
                .isCorrect(userAnswer.getIsCorrect())
                .build();
    }
}
