package com.hcmute.fit.toeicrise.dtos.responses.test.writing;

import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WritingTestDetailResponse {
    private Long id;
    private String name;
    private ETestStatus status;
    private String createdAt;
    private String updatedAt;
    private List<WritingPartResponse> partResponses;
}
