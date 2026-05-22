package com.hcmute.fit.toeicrise.dtos.responses.test.speaking;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SpeakingPartResponse {
    private Long id;
    private String name;
    private List<SpeakingQuestionGroupResponse> questionGroups;
}
