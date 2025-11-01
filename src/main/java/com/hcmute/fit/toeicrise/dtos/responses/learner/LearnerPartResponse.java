package com.hcmute.fit.toeicrise.dtos.responses.learner;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LearnerPartResponse {
    private Long partId;
    private String partName;
    private List<String> tagNames;
}