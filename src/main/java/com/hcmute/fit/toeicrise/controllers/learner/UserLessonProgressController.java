package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.dtos.requests.learningpath.UserLessonProgressUpsertRequest;
import com.hcmute.fit.toeicrise.services.interfaces.IUserLessonProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("learnerUserLessonProgressController")
@RequestMapping("/learner/lesson-progress")
@RequiredArgsConstructor
public class UserLessonProgressController {
    private final IUserLessonProgressService userLessonProgressService;

    @PostMapping
    public ResponseEntity<?> upsert(@Valid @RequestBody UserLessonProgressUpsertRequest request) {
        String email = SecurityUtils.getCurrentUser();
        userLessonProgressService.upsertProgress(email, request);
        return ResponseEntity.ok(MessageConstant.USER_LESSON_PROGRESS_UPDATED_SUCCESS);
    }
}
