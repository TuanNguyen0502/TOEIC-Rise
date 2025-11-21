package com.hcmute.fit.toeicrise.dtos.responses.analysis;

import com.hcmute.fit.toeicrise.dtos.responses.usertest.UserAnswerGroupedByTagResponse;
import lombok.*;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExamTypeStatsResponse {
    private int numberOfTests;
    private Long timeSpent;
    private int averageScore;
    private int maxScore;
    Map<String, List<UserAnswerGroupedByTagResponse>> userAnswersByPart;
}
