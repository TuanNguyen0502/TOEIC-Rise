package com.hcmute.fit.toeicrise.controllers.staff;

import com.hcmute.fit.toeicrise.dtos.requests.question.SWQuestionGroupUpdateRequest;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/staff/writing-question-groups")
@RequiredArgsConstructor
public class WritingQuestionGroupController {
    private final IQuestionGroupService questionGroupService;

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateQuestionGroup(@PathVariable Long id, @Valid @ModelAttribute SWQuestionGroupUpdateRequest request) {
        return ResponseEntity.ok(questionGroupService.updateWritingQuestionGroup(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionGroup(@PathVariable Long id) {
        return ResponseEntity.ok(questionGroupService.getWritingQuestionGroupResponse(id));
    }
}
