package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.requests.question.SpeakingQuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.requests.question.WritingQuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestQuestionGroupResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestQuestionGroupWithoutTranscriptResponse;
import com.hcmute.fit.toeicrise.dtos.responses.minitest.MiniTestQuestionGroupAnswerResponse;
import com.hcmute.fit.toeicrise.dtos.responses.minitest.MiniTestQuestionGroupResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.speaking.SpeakingQuestionGroupResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.speaking.SpeakingQuestionResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.writing.WritingQuestionGroupResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.writing.WritingQuestionResponse;
import com.hcmute.fit.toeicrise.models.entities.Part;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.entities.Test;
import com.hcmute.fit.toeicrise.dtos.responses.test.QuestionGroupResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.QuestionResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionGroupMapper {
    @Mapping(target = "position", source = "position")
    LearnerTestQuestionGroupResponse toLearnerTestQuestionGroupResponse(QuestionGroup questionGroup);

    LearnerTestQuestionGroupWithoutTranscriptResponse toLearnerTestQuestionGroupWithoutTranscriptResponse(QuestionGroup questionGroup);

    @Mapping(target = "questions", ignore = true)
    MiniTestQuestionGroupResponse toMiniTestQuestionGroupResponse(QuestionGroup questionGroup);

    @Mapping(target = "questions", ignore = true)
    MiniTestQuestionGroupAnswerResponse toMiniTestQuestionGroupAnswerResponse(QuestionGroup questionGroup);

    default QuestionGroupResponse toResponse(QuestionGroup questionGroup, List<QuestionResponse> questions) {
        return QuestionGroupResponse.builder()
                .id(questionGroup.getId())
                .audioUrl(questionGroup.getAudioUrl())
                .imageUrl(questionGroup.getImageUrl())
                .passage(questionGroup.getPassage())
                .transcript(questionGroup.getTranscript())
                .position(questionGroup.getPosition())
                .questions(questions)
                .build();
    }

    default SpeakingQuestionGroupResponse toSpeakingQuestionGroupResponse(QuestionGroup questionGroup, List<SpeakingQuestionResponse> questions) {
        return SpeakingQuestionGroupResponse.builder()
                .id(questionGroup.getId())
                .imageUrl(questionGroup.getImageUrl())
                .passage(questionGroup.getPassage())
                .position(questionGroup.getPosition())
                .questions(questions)
                .build();
    }

    default WritingQuestionGroupResponse toWritingQuestionGroupResponse(QuestionGroup questionGroup, List<WritingQuestionResponse> questions) {
        return WritingQuestionGroupResponse.builder()
                .id(questionGroup.getId())
                .imageUrl(questionGroup.getImageUrl())
                .passage(questionGroup.getPassage())
                .position(questionGroup.getPosition())
                .questions(questions)
                .build();
    }

    default QuestionGroup toQuestionGroup(Test test, Part part, QuestionExcelRequest excelRequest) {
        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setTest(test);
        questionGroup.setPart(part);
        questionGroup.setAudioUrl(excelRequest.getAudioUrl());
        questionGroup.setImageUrl(excelRequest.getImageUrl());
        questionGroup.setPosition(excelRequest.getNumberOfQuestions());
        questionGroup.setPassage(excelRequest.getPassageText());
        questionGroup.setTranscript(excelRequest.getTranscript());
        return questionGroup;
    }

    default QuestionGroup toQuestionGroup(Test test, Part part, SpeakingQuestionExcelRequest excelRequest) {
        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setTest(test);
        questionGroup.setPart(part);
        questionGroup.setImageUrl(excelRequest.getImageUrl());
        questionGroup.setPosition(excelRequest.getNumberOfQuestions());
        questionGroup.setPassage(excelRequest.getPassageText());
        return questionGroup;
    }

    default QuestionGroup toQuestionGroup(Test test, Part part, WritingQuestionExcelRequest excelRequest) {
        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setTest(test);
        questionGroup.setPart(part);
        questionGroup.setImageUrl(excelRequest.getImageUrl());
        questionGroup.setPosition(excelRequest.getNumberOfQuestions());
        questionGroup.setPassage(excelRequest.getPassageText());
        return questionGroup;
    }
}