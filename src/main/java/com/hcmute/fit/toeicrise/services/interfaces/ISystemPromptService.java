package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.SystemPromptCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.SystemPromptUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.SystemPromptDetailResponse;

public interface ISystemPromptService {
    PageResponse getAllSystemPrompts(Boolean isActive,
                                     Integer version,
                                     int page,
                                     int size,
                                     String sortBy,
                                     String direction);

    SystemPromptDetailResponse getSystemPromptById(Long id);

    SystemPromptDetailResponse getActiveSystemPrompt();

    boolean updateSystemPrompt(Long id, SystemPromptUpdateRequest request);

    boolean createSystemPrompt(SystemPromptCreateRequest request);

    boolean changeActive(Long id);
}
