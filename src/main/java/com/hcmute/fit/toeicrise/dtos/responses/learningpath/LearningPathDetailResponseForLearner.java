package com.hcmute.fit.toeicrise.dtos.responses.learningpath;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LearningPathDetailResponseForLearner {
    private Long id;
    private String name;
    private String slug;
    private List<LessonWithProgressResponse> lessons;
}
