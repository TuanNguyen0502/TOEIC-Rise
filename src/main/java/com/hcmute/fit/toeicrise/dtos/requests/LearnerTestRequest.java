package com.hcmute.fit.toeicrise.dtos.requests;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LearnerTestRequest {
    private List<Long> parts;
}