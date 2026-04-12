package com.hcmute.fit.toeicrise.controllers.admin;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LearningPathCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LearningPathUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LessonCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LessonReorderRequest;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LessonUpdateRequest;
import com.hcmute.fit.toeicrise.services.interfaces.ILearningPathService;
import com.hcmute.fit.toeicrise.services.interfaces.ILessonService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController("adminLearningPathController")
@RequestMapping("/admin/learning-paths")
@RequiredArgsConstructor
public class LearningPathController {
    private final ILearningPathService learningPathService;
    private final ILessonService lessonService;

    @GetMapping
    public ResponseEntity<?> getAllLearningPaths(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        return ResponseEntity.ok(learningPathService.getAllLearningPaths(name, page, size, sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        return ResponseEntity.ok(learningPathService.getLearningPathDetail(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody LearningPathCreateRequest request) {
        learningPathService.createLearningPath(request);
        return ResponseEntity.ok(MessageConstant.LEARNING_PATH_CREATED_SUCCESS);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody LearningPathUpdateRequest request) {
        learningPathService.updateLearningPath(id, request);
        return ResponseEntity.ok(MessageConstant.LEARNING_PATH_UPDATED_SUCCESS);
    }

    @PostMapping("/{learningPathId}/lessons")
    public ResponseEntity<?> addLesson(
            @PathVariable Long learningPathId,
            @Valid @RequestPart("request") LessonCreateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return ResponseEntity.ok(learningPathService.createLesson(learningPathId, request, file));
    }

    @PutMapping("/lessons/{id}")
    public ResponseEntity<?> updateLesson(@PathVariable Long id, @Valid @RequestPart LessonUpdateRequest request, @RequestPart MultipartFile file) {
        return ResponseEntity.ok(lessonService.updateLesson(id, request, file));
    }

    @PatchMapping("/lessons/{lessonId}/active")
    public ResponseEntity<?> setLessonActive(@PathVariable Long lessonId, @RequestParam("isActive") Boolean isActive) {
        lessonService.setLessonActive(lessonId, isActive);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{learningPathId}/lessons/reorder")
    public ResponseEntity<?> reorder(@PathVariable Long learningPathId, @Valid @RequestBody LessonReorderRequest request) {
        learningPathService.reorderLessons(learningPathId, request);
        return ResponseEntity.ok().build();
    }
}
