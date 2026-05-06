package com.hcmute.fit.toeicrise.controllers.admin;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LessonCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LessonReorderRequest;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.LessonUpdateRequest;
import com.hcmute.fit.toeicrise.models.enums.ELessonLevel;
import com.hcmute.fit.toeicrise.services.interfaces.ILearningPathService;
import com.hcmute.fit.toeicrise.services.interfaces.ILessonService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("adminLessonController")
@RequestMapping("/admin/lessons")
@RequiredArgsConstructor
public class LessonController {
    private final ILessonService lessonService;
    private final ILearningPathService learningPathService;

    @GetMapping("/learning-path/{path-learning-slug}")
    public ResponseEntity<?> detail(@PathVariable(name = "path-learning-slug") String pathLearningSlug,
                                    @RequestParam(required = false) String name,
                                    @RequestParam(required = false) ELessonLevel level,
                                    @RequestParam(defaultValue = "0")
                                    @Min(value = 0) int page,
                                    @RequestParam(defaultValue = "10")
                                    @Min(1) @Max(100) int size,
                                    @RequestParam(defaultValue = "orderIndex") String sortBy,
                                    @RequestParam(defaultValue = "ASC") String direction)  {
        return ResponseEntity.ok(learningPathService.getLearningPathDetail(pathLearningSlug, name, level, page, size, sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLesson(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(lessonService.getLesson(id, SecurityUtils.getCurrentUser()));
    }

    @PostMapping(value = "/{learning-path-slug}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addLesson(
            @PathVariable(value = "learning-path-slug") String learningPathSlug,
            @Valid @ModelAttribute LessonCreateRequest request
    ) {
        return ResponseEntity.ok(lessonService.createLesson(learningPathSlug, SecurityUtils.getCurrentUser(), request));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateLesson(@PathVariable Long id, @Valid @ModelAttribute LessonUpdateRequest request) {
        return ResponseEntity.ok(lessonService.updateLesson(id, request));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<?> setLessonActive(@PathVariable(name = "id") Long id, @RequestParam("isActive") Boolean isActive) {
        lessonService.setLessonActive(id, isActive);
        return ResponseEntity.ok(MessageConstant.LESSON_ACTIVE_UPDATE_SUCCESS);
    }

    @PostMapping("/reorder")
    public ResponseEntity<?> reorder(@Valid @RequestBody LessonReorderRequest request) {
        lessonService.reorderLesson(request);
        return ResponseEntity.ok(MessageConstant.LESSON_REORDER_SUCCESS);
    }
}
