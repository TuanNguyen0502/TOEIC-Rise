package com.hcmute.fit.toeicrise.dtos.responses;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
@Builder
public class TestSetDetailResponse {
    private Long id;
    private String name;
    private String status;
    private String createdAt;
    private String updatedAt;
    private Page<TestResponse> testResponses;
}
