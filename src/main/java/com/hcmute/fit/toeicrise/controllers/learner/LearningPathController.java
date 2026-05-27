package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.models.enums.ELessonLevel;
import com.hcmute.fit.toeicrise.models.enums.ETestType;
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

    @GetMapping("/{learningPathSlug}")
    public ResponseEntity<?> detail(@PathVariable (name = "learningPathSlug") String learningPathSlug) {
        String email = SecurityUtils.getCurrentUser();
        return ResponseEntity.ok(learningPathService.getLearningPathDetailForLearner(email, learningPathSlug));
    }

    @GetMapping("/{learningPathSlug}/level-learning-path")
    public ResponseEntity<?> getLevelForLearningPath(@PathVariable (name = "learningPathSlug") String learningPathSlug, @RequestParam (name = "testType") ETestType testType) {
        String email = SecurityUtils.getCurrentUser();
        return ResponseEntity.ok(learningPathService.getLearningPathLevel(learningPathSlug, email, testType));
    }

    @PostMapping("/{learningPathSlug}")
    public ResponseEntity<?> createLevelForLearningPath(@PathVariable (name = "learningPathSlug") String learningPathSlug, ELessonLevel level){
        String email = SecurityUtils.getCurrentUser();
        learningPathService.createUserLearningPath(email, learningPathSlug, level);
        return ResponseEntity.ok(MessageConstant.USER_LESSON_CREATED_SUCCESS);
    }

    @GetMapping("/lessons/{lessonSlug}")
    public ResponseEntity<?> getLesson(@PathVariable (name = "lessonSlug") String lessonSlug, @RequestParam(name = "learningPathSlug") String learningPathSlug) {
        String email = SecurityUtils.getCurrentUser();
        return ResponseEntity.ok(lessonService.getLesson(lessonSlug, email, learningPathSlug));
    }
}
