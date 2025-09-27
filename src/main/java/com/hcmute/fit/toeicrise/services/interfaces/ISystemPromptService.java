package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.SystemPromptCreateRequest;
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

    boolean createSystemPrompt(SystemPromptCreateRequest request);
}
