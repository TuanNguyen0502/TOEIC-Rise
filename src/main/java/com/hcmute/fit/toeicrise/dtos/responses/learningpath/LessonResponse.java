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
public class LessonResponse {
    private Long id;
    private Long learningPathId;
    private String title;
    private String videoUrl;
    private String topic;
    private ELessonLevel level;
    private String content;
    private Boolean isActive;
    private Integer orderIndex;
    private String updatedAt;
}
