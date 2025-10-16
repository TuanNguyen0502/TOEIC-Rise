package com.hcmute.fit.toeicrise.controllers.admin;

import com.hcmute.fit.toeicrise.services.interfaces.IQuestionGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/groups")
@RequiredArgsConstructor
public class GroupController {
    private final IQuestionGroupService questionGroupService;

    @GetMapping()
    public ResponseEntity<?> getAllQuestionGroupsByPartId(@RequestParam Long testId,
                                                          @RequestParam Long partId) {
        return ResponseEntity.ok(questionGroupService.getQuestionGroupsByTestIdAndPartId(testId, partId));
    }
}