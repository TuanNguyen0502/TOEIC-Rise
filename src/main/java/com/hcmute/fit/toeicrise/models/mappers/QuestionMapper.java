package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.requests.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.requests.QuestionRequest;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.dtos.responses.QuestionResponse;
import com.hcmute.fit.toeicrise.models.entities.Tag;
import org.mapstruct.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    @Mapping(source = "tags", target = "tags", qualifiedByName = "mapTagsToNames")
    QuestionResponse toQuestionResponse(Question question);

    default Question toEntity(QuestionExcelRequest excelRequest, QuestionGroup questionGroup) {
        List<String> options = Arrays.asList(
                excelRequest.getOptionA(),
                excelRequest.getOptionB(),
                excelRequest.getOptionC(),
                excelRequest.getOptionD()
        );
        return Question.builder()
                .questionGroup(questionGroup)
                .position(excelRequest.getNumberOfQuestions())
                .content(excelRequest.getQuestion())
                .options(options)
                .correctOption(excelRequest.getCorrectAnswer())
                .explanation(excelRequest.getExplanation())
                .build();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "options", source = "options", qualifiedByName = "mapToList")
    @Mapping(target = "tags", ignore = true)
    Question toEntity(QuestionRequest questionRequest, @MappingTarget Question question);

    @Named("mapToList")
    default List<String> mapToList(Map<String, String> options){
        return options.entrySet().stream().map(entry -> entry.getKey() + ":" + entry.getValue()).collect(Collectors.toList());
    }

    @Named("mapTagsToNames")
    default List<String> mapTagsToNames(List<Tag> tags){
        if (tags == null) return null;
        return tags.stream().map(Tag::getName).collect(Collectors.toList());
    }
}