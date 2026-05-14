package com.hcmute.fit.toeicrise.dtos.responses.learningpath;

import com.hcmute.fit.toeicrise.models.enums.ETestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathSummaryResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private Boolean isActive;
    private Integer lessonCount;
    private ETestType testType;
    private String updatedAt;
}
