package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.responses.QuestionGroupResponse;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.mappers.QuestionGroupMapper;
import com.hcmute.fit.toeicrise.repositories.QuestionGroupRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionGroupService;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionGroupServiceImpl implements IQuestionGroupService {
    private final QuestionGroupRepository questionGroupRepository;
    private final IQuestionService questionService;
    private final QuestionGroupMapper questionGroupMapper;

    @Override
    public List<QuestionGroupResponse> getQuestionGroupsByTestId(Long testId) {
        // Fetch question groups associated with the test
        List<QuestionGroup> questionGroups = questionGroupRepository.findByTest_IdOrderByPositionAsc(testId);
        List<QuestionGroupResponse> questionGroupResponses = new ArrayList<>();
        for (QuestionGroup questionGroup : questionGroups) {
            // Count questions in each question group
            int questionCount = questionService.countQuestionsByQuestionGroupId(questionGroup.getId());
            // Add to response list
            questionGroupResponses.add(questionGroupMapper.toResponse(questionGroup, questionCount));
        }
        return questionGroupResponses;
    }
}
