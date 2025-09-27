package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.SystemPromptCreateRequest;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.SystemPrompt;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.repositories.SystemPromptRepository;
import com.hcmute.fit.toeicrise.services.interfaces.ISystemPromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SystemPromptServiceImpl implements ISystemPromptService {
    private final SystemPromptRepository systemPromptRepository;

    @Override
    public boolean createSystemPrompt(SystemPromptCreateRequest request) {
        SystemPrompt lastestVersion = systemPromptRepository.findLatestVersion().orElse(null);
        // If there is an existing version, ensure the new version is greater
        if (lastestVersion != null && request.getVersion() <= lastestVersion.getVersion()) {
            // New version must be greater than the latest version
            throw new AppException(ErrorCode.SYSTEM_PROMPT_VERSION_INVALID);
        }

        SystemPrompt activePrompt = systemPromptRepository.findFirstByIsActive(true).orElse(null);
        // Deactivate the current active prompt if it exists
        if (activePrompt != null) {
            activePrompt.setIsActive(false);
            systemPromptRepository.save(activePrompt);
        }

        // Create and save the new system prompt as active
        SystemPrompt newPrompt = SystemPrompt.builder()
                .version(request.getVersion())
                .content(request.getContent())
                .isActive(true)
                .build();
        systemPromptRepository.save(newPrompt);
        return true;
    }
}
