package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.repositories.SystemPromptRepository;
import com.hcmute.fit.toeicrise.services.interfaces.ISystemPromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SystemPromptServiceImpl implements ISystemPromptService {
    private final SystemPromptRepository systemPromptRepository;
}
