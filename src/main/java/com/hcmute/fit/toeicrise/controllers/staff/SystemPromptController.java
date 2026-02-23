package com.hcmute.fit.toeicrise.controllers.staff;

import com.hcmute.fit.toeicrise.dtos.requests.chatbot.SystemPromptCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.chatbot.SystemPromptUpdateRequest;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.enums.ESystemPromptFeatureType;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.services.impl.AbstractSystemPromptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/staff/system-prompts")
@RequiredArgsConstructor
public class SystemPromptController {
    private final List<AbstractSystemPromptService> systemPromptServices;

    @GetMapping("")
    public ResponseEntity<?> getAllSystemPrompts(
            @RequestParam(value = "featureType") ESystemPromptFeatureType featureType,
            @RequestParam(value = "isActive", required = false) Boolean isActive,
            @RequestParam(value = "version", required = false) Integer version,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "version") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") String direction
    ) {
        AbstractSystemPromptService service = systemPromptServices.stream()
                .filter(s -> s.getFeatureType() == featureType)
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST, "Invalid feature type"));
        return ResponseEntity.ok(service.getAllSystemPrompts(isActive, version, page, size, sortBy, direction));
    }

    @GetMapping("/{featureType}/{id}")
    public ResponseEntity<?> getSystemPromptById(@PathVariable Long id, @PathVariable ESystemPromptFeatureType featureType) {
        AbstractSystemPromptService service = systemPromptServices.stream()
                .filter(s -> s.getFeatureType() == featureType)
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST, "Invalid feature type"));
        return ResponseEntity.ok(service.getSystemPromptById(id));
    }

    @PutMapping("/{featureType}/{id}")
    public ResponseEntity<?> updateSystemPrompt(@PathVariable Long id,
                                                @Valid @RequestBody SystemPromptUpdateRequest request,
                                                @PathVariable ESystemPromptFeatureType featureType) {
        AbstractSystemPromptService service = systemPromptServices.stream()
                .filter(s -> s.getFeatureType() == featureType)
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST, "Invalid feature type"));
        service.updateSystemPrompt(id, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{featureType}/{id}")
    public ResponseEntity<?> changeActive(@PathVariable Long id, @PathVariable ESystemPromptFeatureType featureType) {
        AbstractSystemPromptService service = systemPromptServices.stream()
                .filter(s -> s.getFeatureType() == featureType)
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST, "Invalid feature type"));
        service.changeActive(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{featureType}")
    public ResponseEntity<?> createSystemPrompt(@Valid @RequestBody SystemPromptCreateRequest request,
                                                @PathVariable ESystemPromptFeatureType featureType) {
        AbstractSystemPromptService service = systemPromptServices.stream()
                .filter(s -> s.getFeatureType() == featureType)
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST, "Invalid feature type"));
        service.createSystemPrompt(request);
        return ResponseEntity.ok().build();
    }
}
