package com.hcmute.fit.toeicrise.dtos.responses.test;

import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestDetailResponse {
    private Long id;
    private String name;
    private ETestStatus status;
    private String createdAt;
    private String updatedAt;
    private List<PartResponse> partResponses;
}
