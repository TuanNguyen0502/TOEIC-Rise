package com.hcmute.fit.toeicrise.controllers.admin;

import com.hcmute.fit.toeicrise.dtos.requests.QuestionGroupUpdateRequest;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/question-groups")
@RequiredArgsConstructor
public class QuestionGroupController {
    private final IQuestionGroupService questionGroupService;

    @PutMapping("/{id}")
    public void updateQuestionGroup(@PathVariable Long id, @ModelAttribute QuestionGroupUpdateRequest request) {
        questionGroupService.updateQuestionGroup(id, request);
    }
}
