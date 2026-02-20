package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.chatbot.SystemPromptCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.chatbot.SystemPromptUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.chatbot.SystemPromptDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.chatbot.SystemPromptResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.SystemPrompt;
import com.hcmute.fit.toeicrise.models.enums.ESystemPromptFeatureType;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.models.mappers.SystemPromptMapper;
import com.hcmute.fit.toeicrise.repositories.SystemPromptRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.SystemPromptSpecification;
import com.hcmute.fit.toeicrise.services.interfaces.IRedisService;
import com.hcmute.fit.toeicrise.services.interfaces.ISystemPromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static com.hcmute.fit.toeicrise.commons.constants.Constant.ACTIVE_PROMPT_KEY;
import static com.hcmute.fit.toeicrise.commons.constants.Constant.CHATBOT_SYSTEM_PROMPT_CACHE;
import static com.hcmute.fit.toeicrise.commons.constants.Constant.CACHE_DURATION;

@Service
@RequiredArgsConstructor
public class SystemPromptServiceImpl implements ISystemPromptService {
    private final SystemPromptRepository systemPromptRepository;
    private final SystemPromptMapper systemPromptMapper;
    private final PageResponseMapper pageResponseMapper;
    private final IRedisService redisService;


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
    public SystemPromptDetailResponse getActiveChatbotSystemPrompt() {
        // Try to get from cache first
        SystemPromptDetailResponse cachedPrompt = redisService.get(
                CHATBOT_SYSTEM_PROMPT_CACHE,
                ACTIVE_PROMPT_KEY,
                SystemPromptDetailResponse.class);

        if (cachedPrompt != null) {
            return cachedPrompt;
        }

        // If not in cache, get from database and cache it
        SystemPrompt activePrompt = systemPromptRepository.findFirstByIsActiveAndFeatureType(true, ESystemPromptFeatureType.CHATBOT)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Active System Prompt"));

        SystemPromptDetailResponse response = systemPromptMapper.toDetailResponse(activePrompt);

        // Cache the result
        redisService.put(CHATBOT_SYSTEM_PROMPT_CACHE, ACTIVE_PROMPT_KEY, response, CACHE_DURATION);

        return response;
    }

    @Override
    public void updateSystemPrompt(Long id, SystemPromptUpdateRequest request) {
        SystemPrompt existingPrompt = systemPromptRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "System Prompt"));

        // If the updated prompt is set to active, deactivate the current active prompt
        if (request.getIsActive()) {
            deactivateChatbotSystemPrompt();
        } else if (existingPrompt.getIsActive()) {
            // Prevent deactivating the only active prompt
            throw new AppException(ErrorCode.SYSTEM_PROMPT_CANNOT_DEACTIVATE);
        }

        // Fetch the latest version to determine the new version number
        SystemPrompt latestVersion = systemPromptRepository.findLatestVersion().orElse(null);
        if (latestVersion == null) {
            latestVersion = existingPrompt; // If no other versions exist, keep the current one
        }

        // Create a new SystemPrompt entity with updated details and incremented version
        SystemPrompt systemPrompt = new SystemPrompt();
        systemPrompt.setContent(request.getContent());
        systemPrompt.setVersion(latestVersion.getVersion() + 1);
        systemPrompt.setIsActive(request.getIsActive());
        systemPromptRepository.save(systemPrompt);

        // If the new system prompt is active, update cache
        if (request.getIsActive()) {
            updateChatbotActivePromptCache(systemPrompt);
        }
    }

    @Override
    public void createSystemPrompt(SystemPromptCreateRequest request) {
        // Deactivate the current active prompt
        deactivateChatbotSystemPrompt();
        // Fetch the latest version to determine the new version number
        SystemPrompt latestVersion = systemPromptRepository.findLatestVersion().orElse(null);

        // Create and save the new system prompt as active
        SystemPrompt newPrompt = SystemPrompt.builder()
                .version(latestVersion == null ? 1 : latestVersion.getVersion() + 1)
                .content(request.getContent())
                .isActive(true)
                .build();
        systemPromptRepository.save(newPrompt);

        // Update cache with the new active prompt
        updateChatbotActivePromptCache(newPrompt);
    }

    @Override
    public void changeActive(Long id) {
        SystemPrompt existingPrompt = systemPromptRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "System Prompt"));

        // If the updated prompt is set to active, deactivate the current active prompt
        if (!existingPrompt.getIsActive()) {
            deactivateChatbotSystemPrompt();
            existingPrompt.setIsActive(true);
            systemPromptRepository.save(existingPrompt);

            // Update cache with the new active prompt
            updateChatbotActivePromptCache(existingPrompt);
        } else {
            // Prevent deactivating the only active prompt
            throw new AppException(ErrorCode.SYSTEM_PROMPT_CANNOT_DEACTIVATE);
        }
    }

    private void deactivateChatbotSystemPrompt() {
        SystemPrompt activePrompt = systemPromptRepository.findFirstByIsActiveAndFeatureType(true, ESystemPromptFeatureType.CHATBOT).orElse(null);
        // Deactivate the current active prompt if it exists
        if (activePrompt != null) {
            activePrompt.setIsActive(false);
            systemPromptRepository.save(activePrompt);

            // Remove the active prompt from cache
            redisService.remove(CHATBOT_SYSTEM_PROMPT_CACHE, ACTIVE_PROMPT_KEY);
        }
    }

    private void updateChatbotActivePromptCache(SystemPrompt activePrompt) {
        // Cache the new active prompt (put() will overwrite any existing entry)
        SystemPromptDetailResponse response = systemPromptMapper.toDetailResponse(activePrompt);
        redisService.put(CHATBOT_SYSTEM_PROMPT_CACHE, ACTIVE_PROMPT_KEY, response, CACHE_DURATION);
    }
}
