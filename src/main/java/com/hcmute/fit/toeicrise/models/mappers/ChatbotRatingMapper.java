package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.dtos.responses.chatbot.ChatbotRatingDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.chatbot.ChatbotRatingResponse;
import com.hcmute.fit.toeicrise.dtos.responses.chatbot.ChatbotResponse;
import com.hcmute.fit.toeicrise.models.entities.ChatbotRating;
import com.hcmute.fit.toeicrise.models.enums.EChatbotRating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatbotRatingMapper {
    @Mapping(target = "rating", expression = "java(chatbotRating.getRating().name())")
    @Mapping(source = "createdAt", target = "createdAt", dateFormat = Constant.DATE_TIME_PATTERN)
    ChatbotRatingResponse toChatbotRatingResponse(ChatbotRating chatbotRating);

    default ChatbotRatingDetailResponse toChatbotRatingDetailResponse(
            ChatbotRating chatbotRating,
            List<ChatbotRatingDetailResponse.ChatbotResponse> chatbotResponses) {
        return ChatbotRatingDetailResponse.builder()
                .id(chatbotRating.getId())
                .userEmail(chatbotRating.getUser().getAccount().getEmail())
                .conversationTitle(chatbotRating.getConversationTitle())
                .messageId(chatbotRating.getMessageId())
                .message(chatbotRating.getMessage())
                .rating(chatbotRating.getRating().name())
                .createdAt(chatbotRating.getCreatedAt().format(DateTimeFormatter.ofPattern(Constant.DATE_TIME_PATTERN)))
                .chatbotResponses(chatbotResponses)
                .build();
    }

    default ChatbotRatingDetailResponse.ChatbotResponse toChatbotResponse(
            ChatbotResponse chatbotResponse,
            EChatbotRating chatbotRating) {
        return ChatbotRatingDetailResponse.ChatbotResponse.builder()
                .content(chatbotResponse.getContent())
                .messageType(chatbotResponse.getMessageType())
                .rating(chatbotRating != null ? chatbotRating.name() : null)
                .build();
    }
}
