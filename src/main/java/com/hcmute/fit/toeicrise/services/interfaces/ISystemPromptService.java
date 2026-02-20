package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.chatbot.SystemPromptCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.chatbot.SystemPromptUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.chatbot.SystemPromptDetailResponse;

public interface ISystemPromptService {
    PageResponse getAllSystemPrompts(Boolean isActive,
                                     Integer version,
                                     int page,
                                     int size,
                                     String sortBy,
                                     String direction);

    SystemPromptDetailResponse getSystemPromptById(Long id);

    SystemPromptDetailResponse getActiveChatbotSystemPrompt();

    void updateSystemPrompt(Long id, SystemPromptUpdateRequest request);

    void createSystemPrompt(SystemPromptCreateRequest request);

    void changeActive(Long id);
}
