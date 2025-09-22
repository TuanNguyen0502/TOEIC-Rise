package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.requests.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.entities.Tag;
import org.mapstruct.Mapper;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    default Question toEntity(QuestionExcelRequest excelRequest, QuestionGroup questionGroup, int position, Set<Tag> tags) {
        List<String> options = Arrays.asList(
                excelRequest.getOptionA(),
                excelRequest.getOptionB(),
                excelRequest.getOptionC(),
                excelRequest.getOptionD()
        );
        return Question.builder()
                .questionGroup(questionGroup)
                .position(position)
                .content(excelRequest.getQuestion())
                .options(options)
                .correctOption(excelRequest.getCorrectAnswer())
                .explanation(excelRequest.getExplanation())
                .tags(tags)
                .build();
    }
}