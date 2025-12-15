package com.hcmute.fit.toeicrise.dtos.responses.minitest;

import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestQuestionGroupResponse;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MiniTestOverallResponse {
    private int totalQuestions;
    private int correctAnswers;
    private List<MiniTestQuestionGroupResponse> questionGroups;
}
