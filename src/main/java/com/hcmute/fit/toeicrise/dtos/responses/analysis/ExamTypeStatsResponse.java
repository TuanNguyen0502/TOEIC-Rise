package com.hcmute.fit.toeicrise.dtos.responses.analysis;

import com.hcmute.fit.toeicrise.dtos.requests.usertest.PartStats;
import com.hcmute.fit.toeicrise.dtos.requests.usertest.TagStats;
import com.hcmute.fit.toeicrise.dtos.responses.usertest.UserAnswerGroupedByTagResponse;
import lombok.*;

import java.util.*;

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

    public ExamTypeStatsResponse buildExamTypeStatsResponse(
            int totalQuestionExamType,
            int correctAnswerExamType,
            Map<String, Map<String, TagStats>> rawDataByPart,
            Map<String, PartStats> rawPartStats
    ) {
        Map<String, List<UserAnswerGroupedByTagResponse>> userAnswersByPart = new HashMap<>();
        double overallCorrectPercent = totalQuestionExamType == 0 ? 0.0 : ((double) correctAnswerExamType / totalQuestionExamType) * 100;

        if (rawDataByPart != null) {
            for (Map.Entry<String, Map<String, TagStats>> partEntry : rawDataByPart.entrySet()) {
                String partName = partEntry.getKey();
                Map<String, TagStats> tagStatsMap = partEntry.getValue();

                List<UserAnswerGroupedByTagResponse> groupedResponses = new ArrayList<>();
                UserAnswerGroupedByTagResponse totalPartResponse = null;

                tagStatsMap.forEach((tag, stats) -> {
                    int total = stats.getCorrect() + stats.getWrong();
                    double correctPercent = total == 0 ? 0.0 : ((double) stats.getCorrect() / total) * 100;
                    groupedResponses.add(UserAnswerGroupedByTagResponse.builder()
                            .tag(tag)
                            .correctAnswers(stats.getCorrect())
                            .wrongAnswers(stats.getWrong())
                            .correctPercent(correctPercent)
                            .userAnswerOverallResponses(null)
                            .build());
                });
                PartStats partStats = rawPartStats.get(partName);
                if (partStats != null) {
                    int totalForPart = partStats.getCorrect() + partStats.getWrong();
                    double totalPercent = totalForPart == 0 ? 0.0 : ((double) partStats.getCorrect() / totalForPart) * 100;

                    totalPartResponse = UserAnswerGroupedByTagResponse.builder()
                            .tag("Total")
                            .correctAnswers(partStats.getCorrect())
                            .wrongAnswers(partStats.getWrong())
                            .correctPercent(totalPercent)
                            .userAnswerOverallResponses(null)
                            .build();
                }
                groupedResponses.sort(Comparator.comparing(UserAnswerGroupedByTagResponse::getCorrectPercent));
                groupedResponses.add(totalPartResponse);
                userAnswersByPart.put(partName, groupedResponses);

            }
        }

        return ExamTypeStatsResponse.builder()
                .totalCorrectAnswers(correctAnswerExamType)
                .totalQuestions(totalQuestionExamType)
                .correctPercent(overallCorrectPercent)
                .userAnswersByPart(userAnswersByPart)
                .build();
    }
}
