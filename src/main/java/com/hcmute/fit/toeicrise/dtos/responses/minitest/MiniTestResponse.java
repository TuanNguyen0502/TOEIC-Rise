package com.hcmute.fit.toeicrise.dtos.responses.minitest;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MiniTestResponse {
    Long totalQuestions;
    List<MiniTestQuestionGroupResponse> questionGroups;
}
