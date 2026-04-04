package com.hcmute.fit.toeicrise.dtos.responses.testset;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.models.enums.ETestSetStatus;
import com.hcmute.fit.toeicrise.models.enums.ETestSetType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestSetDetailResponse {
    private Long id;
    private String name;
    private ETestSetStatus status;
    private ETestSetType type;
    private String createdAt;
    private String updatedAt;
    private PageResponse testResponses;
}
