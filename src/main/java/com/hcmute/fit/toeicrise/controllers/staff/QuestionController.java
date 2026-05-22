package com.hcmute.fit.toeicrise.controllers.staff;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionRequest;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/staff/questions")
@RequiredArgsConstructor
public class QuestionController {
    private final IQuestionService questionService;

    @PutMapping()
    public ResponseEntity<?> updateQuestion(@Valid @RequestBody QuestionRequest questionRequest) {
        questionService.updateQuestion(questionRequest);
        return ResponseEntity.ok(MessageConstant.QUESTION_UPDATE_SUCCESS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestionResponseById(id));
    }
}