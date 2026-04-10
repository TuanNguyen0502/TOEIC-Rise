package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.dictation.DictationImportRequest;
import com.hcmute.fit.toeicrise.dtos.requests.dictation.DictationTranscriptRequest;
import com.hcmute.fit.toeicrise.dtos.requests.dictation.DictationTranscriptUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.dictation.DictationResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.DictationTranscript;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.entities.Test;
import com.hcmute.fit.toeicrise.models.enums.EPart;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.DictationTranscriptMapper;
import com.hcmute.fit.toeicrise.models.mappers.QuestionMapper;
import com.hcmute.fit.toeicrise.repositories.DictationTranscriptRepository;
import com.hcmute.fit.toeicrise.repositories.QuestionGroupRepository;
import com.hcmute.fit.toeicrise.repositories.TestRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IDictationTranscriptService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DictationTranscriptServiceImpl implements IDictationTranscriptService {
    DictationTranscriptRepository dictationTranscriptRepository;
    TestRepository testRepository;
    QuestionGroupRepository questionGroupRepository;
    DictationTranscriptRepository dictationRepository;
    DictationTranscriptMapper dictationTranscriptMapper;


    @Override
    @Transactional
    public void importDictationTranscript(DictationImportRequest mainRequest) {
        Long testId = mainRequest.getTestId();
        Long partId = mainRequest.getPartId();
        List<DictationTranscriptRequest> transcriptRequests = mainRequest.getTranscripts();

        List<Long> requestGroupIds = transcriptRequests.stream()
                .map(DictationTranscriptRequest::getQuestionGroupId)
                .toList();

        List<QuestionGroup> dbGroups = questionGroupRepository.findAllByValidationInfo(
                requestGroupIds, testId, partId
        );

        if (dbGroups.size() != requestGroupIds.size()) {
            throw new AppException(ErrorCode.INVALID_DATA,
                    "Data conflict.");
        }

        dictationTranscriptRepository.deleteByQuestionGroupIdIn(requestGroupIds);

        Map<Long, QuestionGroup> groupMap = dbGroups.stream()
                .collect(Collectors.toMap(QuestionGroup::getId, g -> g));

        List<DictationTranscript> newTranscripts = transcriptRequests.stream()
                .map(req -> {
                    DictationTranscript dt = dictationTranscriptMapper.toDictationTranscript(req);
                    dt.setQuestionGroup(groupMap.get(req.getQuestionGroupId()));
                    return dt;
                }).toList();

        dictationTranscriptRepository.saveAll(newTranscripts);

        String partNameFromDb = dbGroups.getFirst().getPart().getName();
        updateTestPartStatus(testId, partNameFromDb);
    }

    @Override
    public List<DictationResponse> getListeningDictationByTestAndPart(Long testId, Long partId) {

        List<QuestionGroup> groups = questionGroupRepository.findByTestIdAndPartIdOrderByPosition(
                testId, partId);

        if(groups.isEmpty()) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test or part");
        }

        List<Long> groupIds = groups.stream().map(QuestionGroup::getId).toList();

        Map<Long, DictationTranscript> dictationMap = dictationRepository.findByQuestionGroupIdIn(groupIds)
                .stream()
                .collect(Collectors.toMap(d -> d.getQuestionGroup().getId(), d -> d));

        return groups.stream().map( gr -> {
            DictationTranscript dt = dictationMap.get(gr.getId());

            return DictationResponse.builder()
                    .id(dt.getId())
                    .questionGroupId(gr.getId())
                    .transcript(gr.getTranscript())
                    .passageText(dt.getPassageText())
                    .questionText(dt.getQuestionText())
                    .options(dt.getOptions())
                    .build();
        }).toList();
    }

    @Override
    public void updateDictationTranscript(DictationTranscriptUpdateRequest request) {
        DictationTranscript existing = dictationTranscriptRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Dictation transcript"));

        existing.setQuestionText(request.getQuestionText());
        existing.setOptions(request.getOptions());
        existing.setPassageText(request.getPassageText());

        dictationTranscriptRepository.save(existing);
    }

    private void updateTestPartStatus(Long testId, String partNameStr) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test"));

        EPart currentPart = EPart.getEPart(partNameStr);

        List<EPart> status = test.getDictationStatus();
        if (status == null) status = new ArrayList<>();

        if (!status.contains(currentPart)) {
            status.add(currentPart);
            test.setDictationStatus(status);
            testRepository.save(test);
        }
    }
}
