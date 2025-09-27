package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.SystemPromptCreateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.SystemPromptResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.SystemPrompt;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.models.mappers.SystemPromptMapper;
import com.hcmute.fit.toeicrise.repositories.SystemPromptRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.SystemPromptSpecification;
import com.hcmute.fit.toeicrise.services.interfaces.ISystemPromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SystemPromptServiceImpl implements ISystemPromptService {
    private final SystemPromptRepository systemPromptRepository;
    private final SystemPromptMapper systemPromptMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    public PageResponse getAllSystemPrompts(Boolean isActive,
                                            Integer version,
                                            int page,
                                            int size,
                                            String sortBy,
                                            String direction) {
        Specification<SystemPrompt> specification = (_, _, cb) -> cb.conjunction();
        if (isActive != null) {
            specification = specification.and(SystemPromptSpecification.isActive(isActive));
        }
        if (version != null) {
            specification = specification.and(SystemPromptSpecification.versionGreaterThan(version));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<SystemPrompt> promptsPage = systemPromptRepository.findAll(specification, pageable);

        // Map entities to responses and truncate content
        Page<SystemPromptResponse> systemPrompts = promptsPage.map(prompt -> {
            SystemPromptResponse response = systemPromptMapper.toResponse(prompt);
            // Truncate content to a shorter version (e.g., first 100 characters + "...")
            if (response.getContent() != null && response.getContent().length() > 100) {
                response.setContent(response.getContent().substring(0, 100) + "...");
            }
            return response;
        });

        // Create PageResponse with meta information and result
        return pageResponseMapper.toPageResponse(systemPrompts);
    }

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
