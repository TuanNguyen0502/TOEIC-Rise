package com.hcmute.fit.toeicrise.dtos.responses.usertest;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
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
