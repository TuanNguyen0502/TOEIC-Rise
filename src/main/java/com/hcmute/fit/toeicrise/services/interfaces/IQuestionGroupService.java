package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.question.*;
import com.hcmute.fit.toeicrise.dtos.responses.learner.speaking.LearnerSpeakingPartDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.writing.LearnerWritingPartDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.QuestionGroupResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestPartResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.speaking.SpeakingPartResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.speaking.SpeakingQuestionGroupResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.writing.WritingPartResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.writing.WritingQuestionGroupResponse;
import com.hcmute.fit.toeicrise.models.entities.Part;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.entities.Test;
import com.hcmute.fit.toeicrise.dtos.responses.test.PartResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IQuestionGroupService {
    List<LearnerSpeakingPartDetailResponse> getLearnerSpeakingPartsByTestIdGroupByParts(Long testId, List<Long> partIds);

    List<LearnerWritingPartDetailResponse> getLearnerWritingPartsByTestIdGroupByParts(Long testId, List<Long> partIds);

    @Transactional(readOnly = true)
    List<PartResponse> getQuestionGroupsByTestIdGroupByPart(Long testId);

    @Transactional
    QuestionGroup createQuestionGroup(Test test, Part part, SpeakingQuestionExcelRequest questionExcelRequest);

    @Transactional
    QuestionGroup createQuestionGroup(Test test, Part part, WritingQuestionExcelRequest questionExcelRequest);

    @Transactional
    QuestionGroupResponse updateQuestionGroup(Long questionGroupId, QuestionGroupUpdateRequest request);

    @Transactional(readOnly = true)
    List<LearnerTestPartResponse> getQuestionGroupsByTestIdGroupByParts(Long testId, List<Long> partIds);

    QuestionGroup createQuestionGroup(Test test, Part part, QuestionExcelRequest request);

    SpeakingQuestionGroupResponse updateSpeakingQuestionGroup(Long questionGroupId, SWQuestionGroupUpdateRequest request);

    WritingQuestionGroupResponse updateWritingQuestionGroup(Long questionGroupId, SWQuestionGroupUpdateRequest request);

    void updateQuestionGroupWithEntity(QuestionGroup questionGroup, QuestionGroupUpdateRequest questionGroupUpdateRequest);

    QuestionGroupResponse getQuestionGroupResponse(Long questionGroupId);

    SpeakingQuestionGroupResponse getSpeakingQuestionGroupResponse(Long questionGroupId);

    WritingQuestionGroupResponse getWritingQuestionGroupResponse(Long questionGroupId);

    QuestionGroup getQuestionGroupEntity(Long questionGroupId);

    Map<Long, String> getPartNamesByQuestionGroupIds(Set<Long> questionGroupIds);

    List<QuestionGroup> findAllByIdsWithQuestions(Set<Long> ids);

    void checkQuestionGroupsExistByIds(List<Long> ids);

    boolean isListeningPart(Part part);

    @Transactional(readOnly = true)
    List<SpeakingPartResponse> getSpeakingQuestionGroupsByTestIdGroupByPart(Long testId);

    @Transactional(readOnly = true)
    List<WritingPartResponse> getWritingQuestionGroupsByTestIdGroupByPart(Long testId);
}