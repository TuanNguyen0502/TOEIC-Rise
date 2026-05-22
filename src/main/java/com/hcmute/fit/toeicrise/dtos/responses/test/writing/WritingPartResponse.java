package com.hcmute.fit.toeicrise.dtos.responses.test.writing;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WritingPartResponse {
    private Long id;
    private String name;
    private List<WritingQuestionGroupResponse> questionGroups;
}
