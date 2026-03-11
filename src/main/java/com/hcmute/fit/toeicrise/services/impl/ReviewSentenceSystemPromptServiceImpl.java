package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.models.enums.ESystemPromptFeatureType;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.models.mappers.SystemPromptMapper;
import com.hcmute.fit.toeicrise.repositories.SystemPromptRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IRedisService;
import org.springframework.stereotype.Service;

import static com.hcmute.fit.toeicrise.commons.constants.Constant.CHATBOT_SYSTEM_PROMPT_CACHE;

@Service
public class ReviewSentenceSystemPromptServiceImpl extends AbstractSystemPromptService{
    public ReviewSentenceSystemPromptServiceImpl(IRedisService redisService, SystemPromptRepository systemPromptRepository, SystemPromptMapper systemPromptMapper, PageResponseMapper pageResponseMapper) {
        super(redisService, systemPromptRepository, systemPromptMapper, pageResponseMapper);
    }

    @Override
    public ESystemPromptFeatureType getFeatureType() {
        return ESystemPromptFeatureType.CHATBOT;
    }

    @Override
    protected String getCacheName() {
        return CHATBOT_SYSTEM_PROMPT_CACHE;
    }

}
