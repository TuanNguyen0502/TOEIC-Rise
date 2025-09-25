package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.requests.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.models.entities.Part;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.entities.Test;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuestionGroupMapper {
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
}