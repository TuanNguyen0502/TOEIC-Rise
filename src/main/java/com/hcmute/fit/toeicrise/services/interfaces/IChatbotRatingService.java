package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.chatbot.ChatbotRatingRequest;
import com.hcmute.fit.toeicrise.dtos.responses.chatbot.ChatbotRatingDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.models.enums.EChatbotRating;

public interface IChatbotRatingService {
    PageResponse getChatbotRatings(
            EChatbotRating rating,
            String conversationTitle,
            int page,
            int size,
            String sortBy,
            String direction);

    int countLikeRating();

    int countDislikeRating();

    ChatbotRatingDetailResponse getChatbotRatingDetail(Long id);

    void createChatbotRating(ChatbotRatingRequest chatbotRatingRequest, String email);
}
