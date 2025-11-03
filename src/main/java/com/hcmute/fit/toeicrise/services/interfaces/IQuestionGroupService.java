package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.requests.QuestionGroupUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.QuestionGroupResponse;
import com.hcmute.fit.toeicrise.models.entities.Part;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.entities.Test;
import com.hcmute.fit.toeicrise.dtos.responses.PartResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IQuestionGroupService {
    QuestionGroup createQuestionGroup(Test test, Part part, QuestionExcelRequest request);

    @Transactional(readOnly = true)
    List<PartResponse> getQuestionGroupsByTestIdGroupByPart(Long testId);

    @Transactional
    void updateQuestionGroup(Long questionGroupId, QuestionGroupUpdateRequest request);

    QuestionGroup getQuestionGroup(Long questionGroupId);

    QuestionGroupResponse getQuestionGroupResponse(Long questionGroupId);

    QuestionGroup getQuestionGroupWithQuestionsEntity(Long questionGroupId);

    String getPartNameByQuestionGroupId(Long questionGroupId);

    boolean isListeningPart(Part part);
}