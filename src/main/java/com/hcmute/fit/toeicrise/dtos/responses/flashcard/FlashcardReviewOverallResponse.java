package com.hcmute.fit.toeicrise.dtos.responses.flashcard;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlashcardReviewOverallResponse {
    private Long totalLearnedWords;
    private Long totalNewWords;
    private Long totalDueWords;
}
