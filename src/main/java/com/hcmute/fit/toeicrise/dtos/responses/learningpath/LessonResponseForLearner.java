package com.hcmute.fit.toeicrise.dtos.responses.learningpath;

import com.hcmute.fit.toeicrise.models.enums.ELessonLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonResponseForLearner {
    private Long id;
    private String title;
    private String slug;
    private String practice;
    private Integer orderIndex;
    private ELessonLevel level;
}
