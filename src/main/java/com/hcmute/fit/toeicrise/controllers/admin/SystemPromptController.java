package com.hcmute.fit.toeicrise.controllers.admin;

import com.hcmute.fit.toeicrise.dtos.requests.SystemPromptCreateRequest;
import com.hcmute.fit.toeicrise.services.interfaces.ISystemPromptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/system-prompts")
@RequiredArgsConstructor
public class SystemPromptController {
    private final ISystemPromptService systemPromptService;

    @PostMapping("")
    public ResponseEntity<?> createSystemPrompt(@Valid @RequestBody SystemPromptCreateRequest request) {
        systemPromptService.createSystemPrompt(request);
        return ResponseEntity.ok().build();
    }
}
