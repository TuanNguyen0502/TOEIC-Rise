package com.hcmute.fit.toeicrise.dtos.responses.usertest;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAnswerGroupedByTagResponse {
    private String tag;
    private int correctAnswers;
    private int wrongAnswers;
    private double correctPercent;
    private List<UserAnswerGroupedByTagResponse.UserAnswerOverallResponse> userAnswerOverallResponses;

    @Data
    @Builder
    public static class UserAnswerOverallResponse {
        private Long userAnswerId;
        private int position;
        private boolean isCorrect;
    }
}
