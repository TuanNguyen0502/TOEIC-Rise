package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.requests.QuestionGroupUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.QuestionGroupResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestPartResponse;
import com.hcmute.fit.toeicrise.models.entities.Part;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.entities.Test;
import com.hcmute.fit.toeicrise.dtos.responses.PartResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IQuestionGroupService {
    QuestionGroup createQuestionGroup(Test test, Part part, QuestionExcelRequest request);

    @Transactional(readOnly = true)
    List<PartResponse> getQuestionGroupsByTestIdGroupByPart(Long testId);

    @Transactional
    void updateQuestionGroup(Long questionGroupId, QuestionGroupUpdateRequest request);

    QuestionGroup getQuestionGroup(Long questionGroupId);

    QuestionGroupResponse getQuestionGroupResponse(Long questionGroupId);

    QuestionGroup getQuestionGroupEntity(Long questionGroupId);

    String getPartNameByQuestionGroupId(Long questionGroupId);

    Map<Long, String> getPartNamesByQuestionGroupIds(Set<Long> questionGroupIds);

    List<QuestionGroup> findAllByIdsWithQuestions(Set<Long> ids);

    void checkQuestionGroupsExistByIds(List<Long> ids);

    boolean isListeningPart(Part part);

    @Transactional(readOnly = true)
    List<LearnerTestPartResponse> getQuestionGroupsByTestIdGroupByParts(Long testId, List<Long> partIds);
}