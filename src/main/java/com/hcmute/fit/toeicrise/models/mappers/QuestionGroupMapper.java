package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.requests.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.models.entities.Part;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.entities.Test;
import com.hcmute.fit.toeicrise.dtos.responses.QuestionGroupResponse;
import com.hcmute.fit.toeicrise.dtos.responses.QuestionResponse;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionGroupMapper {
    QuestionGroupResponse toResponse(QuestionGroup questionGroup, @Context List<QuestionResponse> questions);

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

    @AfterMapping
    default void linkQuestions(@MappingTarget QuestionGroupResponse questionGroupResponse, @Context List<QuestionResponse> questions) {
        questionGroupResponse.setQuestions(questions);
    }
}