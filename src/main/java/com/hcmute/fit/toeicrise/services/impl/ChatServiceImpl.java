package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.responses.SystemPromptDetailResponse;
import com.hcmute.fit.toeicrise.repositories.ChatMemoryRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IChatService;
import com.hcmute.fit.toeicrise.services.interfaces.ISystemPromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements IChatService {
    private final ChatClient chatClient;
    private final ChatMemoryRepository chatMemoryRepository;
    private final ISystemPromptService systemPromptService;

    private String getActiveSystemPrompt() {
        SystemPromptDetailResponse response = systemPromptService.getActiveSystemPrompt();
        return response.getContent();
    }
}
