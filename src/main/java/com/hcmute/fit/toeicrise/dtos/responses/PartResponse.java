package com.hcmute.fit.toeicrise.dtos.responses;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PartResponse {
    private Long id;
    private String name;
    private List<QuestionGroupResponse> questionGroups;
}
