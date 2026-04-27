package com.hcmute.fit.toeicrise.controllers.staff;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.dtos.requests.question.SpeakingQuestionUpdateRequest;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/staff/speaking-questions")
@RequiredArgsConstructor
public class SpeakingQuestionController {
    private final IQuestionService questionService;

    @PutMapping()
    public ResponseEntity<?> updateQuestion(@Valid @RequestBody SpeakingQuestionUpdateRequest questionRequest) {
        questionService.updateSpeakingQuestion(questionRequest);
        return ResponseEntity.ok(MessageConstant.QUESTION_UPDATE_SUCCESS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getSpeakingQuestionResponseById(id));
    }
}