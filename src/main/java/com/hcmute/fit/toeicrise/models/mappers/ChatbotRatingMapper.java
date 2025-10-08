package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.dtos.responses.ChatbotRatingResponse;
import com.hcmute.fit.toeicrise.models.entities.ChatbotRating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatbotRatingMapper {
    @Mapping(source = "createdAt", target = "createdAt", dateFormat = Constant.DATE_TIME_PATTERN)
    ChatbotRatingResponse toChatbotRatingResponse(ChatbotRating chatbotRating);
}
