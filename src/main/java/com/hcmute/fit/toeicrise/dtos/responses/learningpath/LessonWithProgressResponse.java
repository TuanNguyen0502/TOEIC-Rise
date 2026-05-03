package com.hcmute.fit.toeicrise.dtos.responses.learningpath;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonWithProgressResponse {
    private LessonResponseForLearner lesson;
    private Double progressPercentage;
    private Long lastWatchedTimeMs;
    private Boolean isCompleted;
    private String progressUpdatedAt;
    private String createdAt;
}
