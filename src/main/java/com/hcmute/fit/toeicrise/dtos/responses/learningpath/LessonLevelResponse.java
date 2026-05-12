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
public class LessonLevelResponse {
    private ELessonLevel currentLevel;
    private ELessonLevel chooseLevel;
}
