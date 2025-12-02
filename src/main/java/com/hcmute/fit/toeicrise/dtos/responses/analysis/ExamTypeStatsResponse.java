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
    private int totalQuestions;
    private int totalCorrectAnswers;
    private double correctPercent;
    Map<String, List<UserAnswerGroupedByTagResponse>> userAnswersByPart;
}
