package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.responses.PartResponse;
import com.hcmute.fit.toeicrise.dtos.responses.QuestionGroupResponse;
import com.hcmute.fit.toeicrise.models.entities.Part;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.mappers.PartMapper;
import com.hcmute.fit.toeicrise.models.mappers.QuestionGroupMapper;
import com.hcmute.fit.toeicrise.repositories.QuestionGroupRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionGroupServiceImpl implements IQuestionGroupService {
    private final QuestionGroupRepository questionGroupRepository;
    private final QuestionGroupMapper questionGroupMapper;
    private final PartMapper partMapper;

    @Transactional(readOnly = true)
    @Override
    public List<PartResponse> getQuestionGroupsByTestIdGroupByPart(Long testId) {
        List<QuestionGroup> questionGroups = questionGroupRepository.findByTest_IdOrderByPositionAsc(testId);

        // Group question groups by part
        Map<Part, List<QuestionGroup>> groupedByPart = questionGroups.stream()
                .collect(Collectors.groupingBy(QuestionGroup::getPart));

        // Convert the map to a list of PartResponse objects
        return groupedByPart.entrySet().stream()
                .map(entry -> {
                    Part part = entry.getKey();
                    List<QuestionGroup> groups = entry.getValue();

                    // Map each QuestionGroup to QuestionGroupResponse
                    List<QuestionGroupResponse> questionGroupResponses = groups.stream()
                            .map(questionGroupMapper::toResponse)
                            .toList();

                    // Create and return a PartResponse
                    return partMapper.toPartResponse(part, questionGroupResponses);
                })
                .toList();
    }
}
