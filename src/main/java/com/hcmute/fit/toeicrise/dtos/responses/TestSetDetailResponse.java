package com.hcmute.fit.toeicrise.dtos.responses;

import com.hcmute.fit.toeicrise.models.enums.ETestSetStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
@Builder
public class TestSetDetailResponse {
    private Long id;
    private String name;
    private ETestSetStatus status;
    private String createdAt;
    private String updatedAt;
    private Page<TestResponse> testResponses;
}
