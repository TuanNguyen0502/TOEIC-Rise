package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.responses.ChatbotRatingResponse;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.models.entities.ChatbotRating;
import com.hcmute.fit.toeicrise.models.enums.EChatbotRating;
import com.hcmute.fit.toeicrise.models.mappers.ChatbotRatingMapper;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.repositories.ChatbotRatingRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.ChatbotRatingSpecification;
import com.hcmute.fit.toeicrise.services.interfaces.IChatbotRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatbotRatingServiceImpl implements IChatbotRatingService {
    private final ChatbotRatingRepository chatbotRatingRepository;
    private final ChatbotRatingMapper chatbotRatingMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    public PageResponse getChatbotRatings(
            EChatbotRating rating,
            String conversationTitle,
            int page,
            int size,
            String sortBy,
            String direction) {
        Specification<ChatbotRating> spec = (_, _, cb) -> cb.conjunction();
        if (rating != null) {
            spec = spec.and(ChatbotRatingSpecification.hasRating(rating));
        }
        if (conversationTitle != null && !conversationTitle.isEmpty()) {
            spec = spec.and(ChatbotRatingSpecification.hasConversationTitle(conversationTitle));
        }
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ChatbotRatingResponse> chatbotRatingResponses = chatbotRatingRepository.findAll(spec, pageable)
                .map(chatbotRatingMapper::toChatbotRatingResponse);
        return pageResponseMapper.toPageResponse(chatbotRatingResponses);
    }
}
