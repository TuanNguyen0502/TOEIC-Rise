package com.hcmute.fit.toeicrise.dtos.responses.testset;

import com.hcmute.fit.toeicrise.models.enums.ETestSetStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestSetResponse {
    private Long id;
    private String name;
    private ETestSetStatus status;
    private String createdAt;
    private String updatedAt;
}
