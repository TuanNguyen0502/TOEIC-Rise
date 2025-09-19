package com.hcmute.fit.toeicrise.dtos.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestSetResponse {
    private Long id;
    private String name;
    private String status;
    private String createdAt;
    private String updatedAt;
}
