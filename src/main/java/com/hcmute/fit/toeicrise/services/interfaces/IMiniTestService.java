package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.minitest.MiniTestRequest;
import com.hcmute.fit.toeicrise.dtos.responses.minitest.MiniTestOverallResponse;
import com.hcmute.fit.toeicrise.dtos.responses.minitest.MiniTestResponse;

import java.util.Set;

public interface IMiniTestService {
    MiniTestOverallResponse getMiniTestOverallResponse(MiniTestRequest request);
    MiniTestResponse getLearnerTestQuestionGroupResponsesByTags(Long partId, Set<Long> tagIds, int numberQuestion);
}
