package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.dtos.responses.chatbot.SystemPromptDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.chatbot.SystemPromptResponse;
import com.hcmute.fit.toeicrise.models.entities.SystemPrompt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface SystemPromptMapper {
    @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = Constant.DATE_TIME_PATTERN)
    SystemPromptResponse toResponse(SystemPrompt systemPrompt);

    default SystemPromptDetailResponse toDetailResponse(SystemPrompt systemPrompt) {
        if (systemPrompt == null) {
            return null;
        }

        SystemPromptDetailResponse response = new SystemPromptDetailResponse();
        response.setId(systemPrompt.getId());
        response.setFeatureType(systemPrompt.getFeatureType());
        response.setContent(systemPrompt.getContent());
        response.setVersion(systemPrompt.getVersion());
        response.setIsActive(systemPrompt.getIsActive());
        response.setCreatedAt(systemPrompt.getCreatedAt().format(DateTimeFormatter.ofPattern(Constant.DATE_TIME_PATTERN)));
        response.setUpdatedAt(systemPrompt.getUpdatedAt().format(DateTimeFormatter.ofPattern(Constant.DATE_TIME_PATTERN)));
        
        return response;
    }
}
