package com.hcmute.fit.toeicrise.dtos.responses;

import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TestDetailResponse {
    private Long id;
    private String name;
    private ETestStatus status;
    private String createdAt;
    private String updatedAt;
    private List<PartResponse> partResponses;
}
