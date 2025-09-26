package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.requests.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.dtos.responses.QuestionResponse;
import org.mapstruct.Mapper;

import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    QuestionResponse toQuestionResponse(Question question);

    default Question toEntity(QuestionExcelRequest excelRequest, QuestionGroup questionGroup) {
        List<String> options = Arrays.asList(
                excelRequest.getOptionA(),
                excelRequest.getOptionB(),
                excelRequest.getOptionC(),
                excelRequest.getOptionD()
        );
        Question question = Question.builder()
                .questionGroup(questionGroup)
                .position(excelRequest.getNumberOfQuestions())
                .content(excelRequest.getQuestion())
                .options(options)
                .correctOption(excelRequest.getCorrectAnswer())
                .explanation(excelRequest.getExplanation())
                .build();
        return question;
    }
}