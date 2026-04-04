package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionRequest;
import com.hcmute.fit.toeicrise.dtos.requests.question.SpeakingQuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.requests.question.WritingQuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestQuestionGroupWithoutTranscriptResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestQuestionResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.RedoWrongQuestionResponse;
import com.hcmute.fit.toeicrise.dtos.responses.minitest.MiniTestQuestionResponse;
import com.hcmute.fit.toeicrise.dtos.responses.minitest.MiniTestAnswerQuestionResponse;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.dtos.responses.test.QuestionResponse;
import com.hcmute.fit.toeicrise.models.entities.Tag;
import com.hcmute.fit.toeicrise.models.entities.UserAnswer;
import org.mapstruct.*;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {QuestionGroupMapper.class})
public interface QuestionMapper {
    @Mapping(source = "tags", target = "tags", qualifiedByName = "mapTagsToNames")
    QuestionResponse toQuestionResponse(Question question);

    @Mapping(source = "tags", target = "tags", qualifiedByName = "mapTagsToNames")
    @Mapping(source = "options", target = "options")
    MiniTestQuestionResponse toMiniTestQuestionResponse(Question question);

    @Mapping(source = "tags", target = "tags", qualifiedByName = "mapTagsToNames")
    MiniTestAnswerQuestionResponse toMiniTestAnswerQuestionResponse(Question question);

    LearnerTestQuestionResponse toLearnerTestQuestionResponse(Question question);

    RedoWrongQuestionResponse toRedoWrongQuestionResponse(Question question);

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

    default Question toEntity(SpeakingQuestionExcelRequest excelRequest, QuestionGroup questionGroup) {
        return Question.builder()
                .questionGroup(questionGroup)
                .position(excelRequest.getNumberOfQuestions())
                .content(excelRequest.getQuestion())
                .build();
    }

    default Question toEntity(WritingQuestionExcelRequest excelRequest, QuestionGroup questionGroup) {
        return Question.builder()
                .questionGroup(questionGroup)
                .position(excelRequest.getNumberOfQuestions())
                .build();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "tags", ignore = true)
    Question toEntity(QuestionRequest questionRequest, @MappingTarget Question question);

    @Named("mapTagsToNames")
    default List<String> mapTagsToNames(List<Tag> tags) {
        if (tags == null) return null;
        return tags.stream().map(Tag::getName).collect(Collectors.toList());
    }

    default LearnerTestQuestionGroupWithoutTranscriptResponse convertToGroupResponse(Map.Entry<QuestionGroup, List<UserAnswer>> entry, QuestionGroupMapper questionGroupMapper) {
        QuestionGroup questionGroup = entry.getKey();
        List<LearnerTestQuestionResponse> questionResponses = entry.getValue().stream()
                .sorted(Comparator.comparing(question -> question.getQuestion().getPosition()))
                .map(question -> toLearnerTestQuestionResponse(question.getQuestion())).toList();

        LearnerTestQuestionGroupWithoutTranscriptResponse groupResponse = questionGroupMapper.toLearnerTestQuestionGroupWithoutTranscriptResponse(questionGroup);
        groupResponse.setQuestions(new ArrayList<>(questionResponses));
        return groupResponse;
    }
}