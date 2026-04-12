package com.hcmute.fit.toeicrise.services.impl.systemprompt;

import com.hcmute.fit.toeicrise.models.enums.ESystemPromptFeatureType;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.models.mappers.SystemPromptMapper;
import com.hcmute.fit.toeicrise.repositories.SystemPromptRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IRedisService;
import org.springframework.stereotype.Service;

import static com.hcmute.fit.toeicrise.commons.constants.Constant.SPEAKING_ASSESSMENT_SYSTEM_PROMPT_CACHE;

@Service
public class SpeakingAssessmentSystemPromptServiceImpl extends AbstractSystemPromptService {
    public SpeakingAssessmentSystemPromptServiceImpl(IRedisService redisService, SystemPromptRepository systemPromptRepository, SystemPromptMapper systemPromptMapper, PageResponseMapper pageResponseMapper) {
        super(redisService, systemPromptRepository, systemPromptMapper, pageResponseMapper);
    }

    @Override
    public ESystemPromptFeatureType getFeatureType() {
        return ESystemPromptFeatureType.SPEAKING_ASSESSMENT;
    }

    @Override
    protected String getCacheName() {
        return SPEAKING_ASSESSMENT_SYSTEM_PROMPT_CACHE;
    }

}
