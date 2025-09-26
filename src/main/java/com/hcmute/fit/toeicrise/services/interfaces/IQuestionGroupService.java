package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.PartResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IQuestionGroupService {
    @Transactional(readOnly = true)
    List<PartResponse> getQuestionGroupsByTestIdGroupByPart(Long testId);
}
