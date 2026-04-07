package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.dictation.DictationTranscriptRequest;
import com.hcmute.fit.toeicrise.dtos.responses.dictation.ListeningDictationResponse;

import java.util.List;

public interface IDictationTranscriptService {
    void importDictationTranscript(List<DictationTranscriptRequest> requests);
    ListeningDictationResponse getListeningDictationByTestIdAndPartId(Long testId, Long partId);

}
