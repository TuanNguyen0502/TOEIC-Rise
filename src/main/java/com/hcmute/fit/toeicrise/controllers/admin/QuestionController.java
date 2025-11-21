package com.hcmute.fit.toeicrise.controllers.admin;

import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionRequest;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/questions")
@RequiredArgsConstructor
public class QuestionController {
    private final IQuestionService questionService;

    @PutMapping()
    public ResponseEntity<?> updateQuestion(@Valid @RequestBody QuestionRequest questionRequest) {
        questionService.updateQuestion(questionRequest);
        return ResponseEntity.ok("Update question updated successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }
}