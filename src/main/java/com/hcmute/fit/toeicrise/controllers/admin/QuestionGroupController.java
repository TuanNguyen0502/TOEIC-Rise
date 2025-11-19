package com.hcmute.fit.toeicrise.controllers.admin;

import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionGroupUpdateRequest;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/question-groups")
@RequiredArgsConstructor
public class QuestionGroupController {
    private final IQuestionGroupService questionGroupService;

    @PutMapping("/{id}")
    public void updateQuestionGroup(@PathVariable Long id, @Valid @ModelAttribute QuestionGroupUpdateRequest request) {
        questionGroupService.updateQuestionGroup(id, request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionGroup(@PathVariable Long id) {
        return ResponseEntity.ok(questionGroupService.getQuestionGroupResponse(id));
    }
}
