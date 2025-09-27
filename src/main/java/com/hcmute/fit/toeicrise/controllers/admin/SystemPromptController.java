package com.hcmute.fit.toeicrise.controllers.admin;

import com.hcmute.fit.toeicrise.dtos.requests.SystemPromptCreateRequest;
import com.hcmute.fit.toeicrise.services.interfaces.ISystemPromptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/system-prompts")
@RequiredArgsConstructor
public class SystemPromptController {
    private final ISystemPromptService systemPromptService;

    @GetMapping("")
    public ResponseEntity<?> getAllSystemPrompts(
            @RequestParam(value = "isActive", required = false) Boolean isActive,
            @RequestParam(value = "version", required = false) Integer version,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "version") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") String direction
    ) {
        return ResponseEntity.ok(systemPromptService.getAllSystemPrompts(isActive, version, page, size, sortBy, direction));
    }

    @PostMapping("")
    public ResponseEntity<?> createSystemPrompt(@Valid @RequestBody SystemPromptCreateRequest request) {
        systemPromptService.createSystemPrompt(request);
        return ResponseEntity.ok().build();
    }
}
