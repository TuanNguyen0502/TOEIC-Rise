package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.SystemPromptCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.SystemPromptUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.SystemPromptDetailResponse;
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
    public SystemPromptDetailResponse getSystemPromptById(Long id) {
        SystemPrompt systemPrompt = systemPromptRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "System Prompt"));
        return systemPromptMapper.toDetailResponse(systemPrompt);
    }

    @Override
    public boolean updateSystemPrompt(Long id, SystemPromptUpdateRequest request) {
        SystemPrompt existingPrompt = systemPromptRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "System Prompt"));

        // If the updated prompt is set to active, deactivate the current active prompt
        if (request.getIsActive().equals(true) && !request.getIsActive().equals(existingPrompt.getIsActive())) {
            deactivateSystemPrompt();
        } else if (request.getIsActive().equals(false) && existingPrompt.getIsActive().equals(true)) {
            // Prevent deactivating the only active prompt
            throw new AppException(ErrorCode.SYSTEM_PROMPT_CANNOT_DEACTIVATE);
        }

        // Fetch the latest version to determine the new version number
        SystemPrompt lastestVersion = systemPromptRepository.findLatestVersion().orElse(null);

        existingPrompt.setContent(request.getContent());
        existingPrompt.setVersion(lastestVersion == null ? 1 : lastestVersion.getVersion() + 1);
        existingPrompt.setIsActive(request.getIsActive());
        systemPromptRepository.save(existingPrompt);
        return true;
    }

    @Override
    public boolean createSystemPrompt(SystemPromptCreateRequest request) {
        // Deactivate the current active prompt
        deactivateSystemPrompt();
        // Fetch the latest version to determine the new version number
        SystemPrompt lastestVersion = systemPromptRepository.findLatestVersion().orElse(null);

        // Create and save the new system prompt as active
        SystemPrompt newPrompt = SystemPrompt.builder()
                .version(lastestVersion == null ? 1 : lastestVersion.getVersion() + 1)
                .content(request.getContent())
                .isActive(true)
                .build();
        systemPromptRepository.save(newPrompt);
        return true;
    }

    private void deactivateSystemPrompt() {
        SystemPrompt activePrompt = systemPromptRepository.findFirstByIsActive(true).orElse(null);
        // Deactivate the current active prompt if it exists
        if (activePrompt != null) {
            activePrompt.setIsActive(false);
            systemPromptRepository.save(activePrompt);
        }
    }
}
