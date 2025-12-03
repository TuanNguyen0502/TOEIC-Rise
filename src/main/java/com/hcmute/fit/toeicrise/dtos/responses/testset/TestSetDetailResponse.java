package com.hcmute.fit.toeicrise.dtos.responses.testset;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.models.enums.ETestSetStatus;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestSetDetailResponse {
    private Long id;
    private String name;
    private ETestSetStatus status;
    private String createdAt;
    private String updatedAt;
    private PageResponse testResponses;
}
