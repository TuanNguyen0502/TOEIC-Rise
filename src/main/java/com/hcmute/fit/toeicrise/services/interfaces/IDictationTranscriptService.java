package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.dictation.DictationTranscriptRequest;

import java.util.List;

public interface IDictationTranscriptService {
    void importDictationTranscript(List<DictationTranscriptRequest> requests);

}
