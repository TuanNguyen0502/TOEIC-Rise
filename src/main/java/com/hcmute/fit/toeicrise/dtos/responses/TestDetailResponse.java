package com.hcmute.fit.toeicrise.dtos.responses;

import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class TestDetailResponse {
    private Long testId;
    private String testName;
    private ETestStatus testStatus;
    private String createdAt;
    private String updatedAt;
    private Long testSetId;
    private String testSetName;
    private List<QuestionGroupResponse> questionGroups;
    private Page<QuestionResponse> questions;
}
