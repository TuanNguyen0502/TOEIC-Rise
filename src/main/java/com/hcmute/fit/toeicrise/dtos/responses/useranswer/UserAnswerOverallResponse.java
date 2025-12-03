package com.hcmute.fit.toeicrise.dtos.responses.useranswer;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAnswerOverallResponse {
    private Long userAnswerId;
    private int position;
    private String correctAnswer;
    private String userAnswer;
}
