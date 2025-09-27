package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.SystemPromptCreateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;

public interface ISystemPromptService {
    PageResponse getAllSystemPrompts(Boolean isActive,
                                     Integer version,
                                     int page,
                                     int size,
                                     String sortBy,
                                     String direction);

    boolean createSystemPrompt(SystemPromptCreateRequest request);
}
