package com.hcmute.fit.toeicrise.dtos.responses.test;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartResponse {
    private Long id;
    private String name;
    private List<QuestionGroupResponse> questionGroups;
}
