package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.responses.ChatbotResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatbotMapper {
    ChatbotResponse toChatbotResponse(String content, String messageId, String conversationId, String messageType);
}
