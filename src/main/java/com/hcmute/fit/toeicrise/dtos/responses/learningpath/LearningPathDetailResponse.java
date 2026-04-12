package com.hcmute.fit.toeicrise.dtos.responses.learningpath;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathDetailResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    private List<LessonResponse> lessons;
    private String updatedAt;
}
