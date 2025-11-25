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
public class ExamTypeFullTestResponse {
    private int totalQuestions;
    private int totalCorrectAnswers;
    private double correctPercent;
    private double averageScore;
    private int highestScore;
    Map<String, List<UserAnswerGroupedByTagResponse>> userAnswersByPart;
}
