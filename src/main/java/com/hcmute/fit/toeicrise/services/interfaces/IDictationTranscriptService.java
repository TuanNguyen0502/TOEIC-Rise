package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.dictation.DictationImportRequest;
import com.hcmute.fit.toeicrise.dtos.requests.dictation.DictationTranscriptUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.dictation.DictationResponse;
import com.hcmute.fit.toeicrise.dtos.responses.dictation.ListeningDictationResponse;
import com.hcmute.fit.toeicrise.dtos.responses.dictation.TestSetDictationAvailableResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface IDictationTranscriptService {
    void importDictationTranscript(DictationImportRequest request);
    List<DictationResponse> getListeningDictationByTestAndPart(Long testId, Long partId);
    void updateDictationTranscript( DictationTranscriptUpdateRequest request);
    List<TestSetDictationAvailableResponse> getDictationLibrary();
    ListeningDictationResponse getListeningDictation(Long testId, Long partId);
}
