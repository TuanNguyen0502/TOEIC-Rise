package com.hcmute.fit.toeicrise.dtos.responses.test;

import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestResponse {
    private Long id;
    private String name;
    private ETestStatus status;
    private Long numberOfLearnerTests;
    private String createdAt;
    private String updatedAt;
}
