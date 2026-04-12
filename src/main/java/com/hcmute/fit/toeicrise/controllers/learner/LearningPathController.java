package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.services.interfaces.ILearningPathService;
import com.hcmute.fit.toeicrise.services.interfaces.ILessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("learnerLearningPathController")
@RequestMapping("/learner/learning-paths")
@RequiredArgsConstructor
public class LearningPathController {
    private final ILearningPathService learningPathService;
    private final ILessonService lessonService;

    @GetMapping
    public ResponseEntity<?> listActive(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "updatedAt") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") String direction
    ) {
        return ResponseEntity.ok(learningPathService.listActiveLearningPaths(page, size, sortBy, direction));
    }

    @GetMapping("/{learningPathId}")
    public ResponseEntity<?> detail(@PathVariable Long learningPathId) {
        String email = SecurityUtils.getCurrentUser();
        return ResponseEntity.ok(learningPathService.getLearningPathDetailForLearner(email, learningPathId));
    }

    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<?> getLesson(@PathVariable Long lessonId) {
        String email = SecurityUtils.getCurrentUser();
        return ResponseEntity.ok(lessonService.getLessonForLearner(lessonId, email));
    }
}
