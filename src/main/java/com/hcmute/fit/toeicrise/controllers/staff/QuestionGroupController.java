package com.hcmute.fit.toeicrise.controllers.staff;

import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionGroupUpdateRequest;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/staff/question-groups")
@RequiredArgsConstructor
public class QuestionGroupController {
    private final IQuestionGroupService questionGroupService;

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateQuestionGroup(@PathVariable Long id, @Valid @ModelAttribute QuestionGroupUpdateRequest request) {
        return ResponseEntity.ok(questionGroupService.updateQuestionGroup(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionGroup(@PathVariable Long id) {
        return ResponseEntity.ok(questionGroupService.getQuestionGroupResponse(id));
    }
}
