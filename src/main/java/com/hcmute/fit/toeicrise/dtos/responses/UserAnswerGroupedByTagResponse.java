package com.hcmute.fit.toeicrise.dtos.responses;

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
    private List<Long> userAnswerIds;
}
