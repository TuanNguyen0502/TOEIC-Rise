package com.hcmute.fit.toeicrise.dtos.responses.test;

import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestResponse {
    private Long id;
    private String name;
    private ETestStatus status;
    private String createdAt;
    private String updatedAt;
}
