package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.SystemPromptCreateRequest;

public interface ISystemPromptService {
    boolean createSystemPrompt(SystemPromptCreateRequest request);
}
