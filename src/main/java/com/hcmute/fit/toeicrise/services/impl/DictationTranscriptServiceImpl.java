package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.dictation.DictationImportRequest;
import com.hcmute.fit.toeicrise.dtos.requests.dictation.DictationTranscriptRequest;
import com.hcmute.fit.toeicrise.dtos.requests.dictation.DictationTranscriptUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.dictation.*;
import com.hcmute.fit.toeicrise.dtos.responses.test.QuestionResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.DictationTranscript;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.entities.Test;
import com.hcmute.fit.toeicrise.models.entities.TestSet;
import com.hcmute.fit.toeicrise.models.enums.EPart;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.DictationTranscriptMapper;
import com.hcmute.fit.toeicrise.repositories.DictationTranscriptRepository;
import com.hcmute.fit.toeicrise.repositories.QuestionGroupRepository;
import com.hcmute.fit.toeicrise.repositories.TestRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IDictationTranscriptService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
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
    public void importDictationTranscript(DictationImportRequest request) {
        Long testId = request.getTestId();
        Long partId = request.getPartId();

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test"));

        EPart part = EPart.getEPartByPosition(partId.intValue());
        if (!part.isListening()) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Part must be a listening part");
        }

        List<DictationTranscriptRequest> transcriptRequests = request.getTranscripts();
        List<Long> requestGroupIds = transcriptRequests.stream()
                .map(DictationTranscriptRequest::getQuestionGroupId)
                .toList();

        Set<Long> requestGroupIdSet = new HashSet<>(requestGroupIds);
        if (requestGroupIdSet.size() != requestGroupIds.size()) {
            throw new AppException(ErrorCode.INVALID_DATA, "Duplicate questionGroupId in request.");
        }

        List<QuestionGroup> dbGroups = questionGroupRepository.findAllByTestIdAndPartId(testId, partId);
        Set<Long> dbIdsSet = dbGroups.stream()
                .map(QuestionGroup::getId)
                .collect(Collectors.toSet());

        if (!requestGroupIdSet.equals(dbIdsSet)) {
            Set<Long> missingIds = new HashSet<>(dbIdsSet);
            missingIds.removeAll(dbIdsSet);

            Set<Long> extraIds = new HashSet<>(dbIdsSet);
            extraIds.removeAll(dbIdsSet);

            throw new AppException(
                    ErrorCode.INVALID_DATA,
                    "Question group IDs do not match. Missing: " + missingIds + ", Extra: " + extraIds
            );
        }

        Map<Long, QuestionGroup> groupMap = dbGroups.stream()
                .collect(Collectors.toMap(QuestionGroup::getId, Function.identity()));

        dictationTranscriptRepository.deleteByQuestionGroupIdIn(requestGroupIds);

        List<DictationTranscript> newTranscripts = transcriptRequests.stream()
                .map(req -> {
                    DictationTranscript dt = dictationTranscriptMapper.toDictationTranscript(req);
                    dt.setQuestionGroup(groupMap.get(req.getQuestionGroupId()));
                    return dt;
                })
                .toList();

        dictationTranscriptRepository.saveAll(newTranscripts);

        updateTestPartStatus(test, part);
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

    @Override
    public List<TestSetDictationAvailableResponse> getDictationLibrary() {

        List<Test> readyTests = testRepository.findAllActiveWithTestSet();

        return readyTests.stream()
                .filter(t -> t.getDictationStatus() != null && !t.getDictationStatus().isEmpty())
                .collect(Collectors.groupingBy(Test::getTestSet))
                .entrySet().stream()
                .map(entry -> {
                    TestSet ts = entry.getKey();

                    List<TestDictationAvailableResponse> testDtos = entry.getValue().stream()
                            .map(t -> TestDictationAvailableResponse.builder()
                                    .id(t.getId())
                                    .name(t.getName())
                                    .availableParts(t.getDictationStatus())
                                    .build())
                            .toList();

                    return TestSetDictationAvailableResponse.builder()
                            .id(ts.getId())
                            .name(ts.getName())
                            .readyTests(testDtos)
                            .build();
                })
                .toList();
    }

    @Override
    public ListeningDictationResponse getListeningDictation(Long testId, Long partId) {

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test"));

        EPart requestedPart = EPart.getEPartByPosition(partId.intValue());
        if (test.getDictationStatus() == null || !test.getDictationStatus().contains(requestedPart)) {
            throw new AppException(ErrorCode.INVALID_DATA, "Phần thi này chưa hỗ trợ nghe chép chính tả.");
        }

        List<QuestionGroup> groups = questionGroupRepository.findByTestIdAndPartIdsWithQuestionsAndPart(
                testId, List.of(partId));

        if (groups.isEmpty()) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy nội dung cho Part này.");
        }

        List<Long> groupIds = groups.stream().map(QuestionGroup::getId).toList();

        Map<Long, DictationTranscript> dictationMap = dictationTranscriptRepository
                .findByQuestionGroupIdIn(groupIds)
                .stream()
                .collect(Collectors.toMap(d -> d.getQuestionGroup().getId(), d -> d));

        List<QuestionGroupDictationResponse> groupResponses = groups.stream().map(group -> {
            DictationTranscript dictation = dictationMap.get(group.getId());

            List<QuestionResponse> questionResponses = group.getQuestions().stream()
                    .map(q -> QuestionResponse.builder()
                            .id(q.getId())
                            .position(Long.valueOf(q.getPosition()))
                            .content(q.getContent())
                            .options(q.getOptions())
                            .correctOption(q.getCorrectOption())
                            .explanation(q.getExplanation())
                            .tags(q.getTags() != null ? q.getTags().stream().map(tag -> tag.getName()).toList() : List.of())
                            .build())
                    .toList();

            return QuestionGroupDictationResponse.builder()
                    .id(group.getId())
                    .audioUrl(group.getAudioUrl())
                    .imageUrl(group.getImageUrl())
                    .passage(group.getPassage())
                    .transcript(group.getTranscript())
                    .position(group.getPosition())

                    .questionText(dictation != null ? dictation.getQuestionText() : null)
                    .options(dictation != null ? dictation.getOptions() : null)
                    .passageText(dictation != null ? dictation.getPassageText() : null)
                    .questions(questionResponses)
                    .build();
        }).toList();

        return ListeningDictationResponse.builder()
                .id(testId)
                .testName(groups.getFirst().getTest().getName())
                .partName(groups.getFirst().getPart().getName())
                .questionGroups(groupResponses)
                .build();
    }

    private void updateTestPartStatus(Test test, EPart currentPart) {
        List<EPart> status = test.getDictationStatus();
        if (status == null) {
            status = new ArrayList<>();
        }

        if (!status.contains(currentPart)) {
            status.add(currentPart);
            test.setDictationStatus(status);
            testRepository.save(test);
        }
    }
}
