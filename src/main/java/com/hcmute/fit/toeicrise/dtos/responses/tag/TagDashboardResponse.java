package com.hcmute.fit.toeicrise.dtos.responses.tag;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagDashboardResponse {
    private Long id;
    private String name;
    private Long questionCount;
    private Long userAnswerCount;
    private Double correctRate;
}
