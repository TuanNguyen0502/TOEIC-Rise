package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.dictation.DictationTranscriptRequest;
import com.hcmute.fit.toeicrise.dtos.responses.dictation.ListeningDictationResponse;
import com.hcmute.fit.toeicrise.dtos.responses.dictation.QuestionGroupDictationResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.DictationTranscript;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.DictationTranscriptMapper;
import com.hcmute.fit.toeicrise.models.mappers.QuestionMapper;
import com.hcmute.fit.toeicrise.repositories.DictationTranscriptRepository;
import com.hcmute.fit.toeicrise.repositories.QuestionGroupRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IDictationTranscriptService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DictationTranscriptServiceImpl implements IDictationTranscriptService {
    DictationTranscriptRepository dictationTranscriptRepository;
    DictationTranscriptMapper dictationTranscriptMapper;
    QuestionGroupRepository questionGroupRepository;
    QuestionMapper questionMapper;

    @Override
    @Transactional
    public void importDictationTranscript(List<DictationTranscriptRequest> requests) {

        List<Long> questionGroupIds = requests.stream()
                .map(DictationTranscriptRequest::getQuestionGroupId)
                .distinct()
                .toList();

        List<QuestionGroup> groups = questionGroupRepository.findAllById(questionGroupIds);

        // Check if all question group IDs exist
        if (groups.size() != questionGroupIds.size()) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question groups");
        }

        // Check if any transcript already exists for the given question group IDs
        List<DictationTranscript> transcripts = dictationTranscriptRepository.findAllByQuestionGroupIdIn(questionGroupIds);
        if (!transcripts.isEmpty()) {
            // Throw or delete
            throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Transcripts");
        }

        Map<Long, QuestionGroup> groupMap = groups.stream()
                .collect(Collectors.toMap(QuestionGroup::getId, g -> g));

        List<DictationTranscript> newTranscripts = requests.stream()
                .map(request -> {
                    DictationTranscript newDictationTranscript
                            = dictationTranscriptMapper.toDictationTranscript(request);
                    newDictationTranscript.setQuestionGroup(groupMap.get(request.getQuestionGroupId()));
                    return newDictationTranscript;
                })
                .toList();

        dictationTranscriptRepository.saveAll(newTranscripts);
    }

    @Override
    public ListeningDictationResponse getListeningDictationByTestIdAndPartId(Long testId, Long partId) {
        List <QuestionGroup> groups = questionGroupRepository.findListeningDictationData(testId, partId);

        if(groups.isEmpty()) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Listening dictation data");
        }

        String partName = groups.getFirst().getPart().getName();
        List<QuestionGroupDictationResponse> groupResponses = groups.stream()
                .filter(g -> g.getDictationTranscript() != null)
                .map(g -> {
                    DictationTranscript dt = g.getDictationTranscript();

                    return QuestionGroupDictationResponse.builder()
                            .id(g.getId())
                            .audioUrl(g.getAudioUrl())
                            .imageUrl(g.getImageUrl())
                            .passage(g.getPassage())
                            .transcript(g.getTranscript())
                            .position(g.getPosition())

                            .questionText(dt.getQuestionText())
                            .options(dt.getOptions())
                            .passageText(dt.getPassageText())

                            .questions(g.getQuestions().stream()
                                    .map(questionMapper::toQuestionResponse)
                                    .toList())
                            .build();
                })
                .toList();

        return ListeningDictationResponse.builder()
                .partName(partName)
                .questionGroups(groupResponses)
                .build();
    }
}
