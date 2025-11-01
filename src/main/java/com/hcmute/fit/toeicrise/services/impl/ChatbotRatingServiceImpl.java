package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.chatbot.ChatbotRatingRequest;
import com.hcmute.fit.toeicrise.dtos.responses.chatbot.ChatbotRatingDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.chatbot.ChatbotRatingResponse;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.ChatbotRating;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.EChatbotRating;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.ChatbotRatingMapper;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.repositories.ChatMemoryRepository;
import com.hcmute.fit.toeicrise.repositories.ChatTitleRepository;
import com.hcmute.fit.toeicrise.repositories.ChatbotRatingRepository;
import com.hcmute.fit.toeicrise.repositories.UserRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.ChatbotRatingSpecification;
import com.hcmute.fit.toeicrise.services.interfaces.IChatbotRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatbotRatingServiceImpl implements IChatbotRatingService {
    private final ChatbotRatingRepository chatbotRatingRepository;
    private final ChatMemoryRepository chatMemoryRepository;
    private final ChatTitleRepository chatTitleRepository;
    private final UserRepository userRepository;
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

    @Override
    public int countLikeRating() {
        return chatbotRatingRepository.countByRating(EChatbotRating.LIKE);
    }

    @Override
    public int countDislikeRating() {
        return chatbotRatingRepository.countByRating(EChatbotRating.DISLIKE);
    }

    @Override
    public ChatbotRatingDetailResponse getChatbotRatingDetail(Long id) {
        // Get chatbot rating
        ChatbotRating chatbotRating = chatbotRatingRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Chatbot rating"));
        // If messageId is null, return empty chat history
        if (chatbotRating.getMessageId() == null) {
            return chatbotRatingMapper.toChatbotRatingDetailResponse(chatbotRating, List.of());
        }
        // Get chat history
        String conversationId = chatMemoryRepository.getConversationIdByMessageId(chatbotRating.getMessageId());
        List<ChatbotRatingDetailResponse.ChatbotResponse> chatbotResponses = chatMemoryRepository.getChatHistory(conversationId)
                .stream()
                .map(chatMemory -> {
                    ChatbotRating rating = chatbotRatingRepository.findFirstByMessageId(chatMemory.getMessageId()).orElse(null);
                    return chatbotRatingMapper.toChatbotResponse(chatMemory, rating != null ? rating.getRating() : null);
                })
                .toList();

        return chatbotRatingMapper.toChatbotRatingDetailResponse(chatbotRating, chatbotResponses);
    }

    @Override
    public void createChatbotRating(ChatbotRatingRequest chatbotRatingRequest, String email) {
        // Check if the messageId exists in chat memory
        if (chatbotRatingRepository.existsByMessageId(chatbotRatingRequest.getMessageId())) {
            return;
        }
        if (!chatMemoryRepository.existsByMessageId(chatbotRatingRequest.getMessageId())) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Chat message");
        }

        // Get user
        User user = userRepository.findByAccount_Email(email).orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
        // Get conversation title
        String conversationId = chatMemoryRepository.getConversationIdByMessageId(chatbotRatingRequest.getMessageId());
        String conversationTitle = chatTitleRepository.findByConversationIdAndUser_Id(conversationId, user.getId())
                .map(chatTitle -> chatTitle.getTitle() != null ? chatTitle.getTitle() : "No title")
                .orElse("No title");
        // Get message
        String message = chatMemoryRepository.getMessageById(chatbotRatingRequest.getMessageId()).getText();
        // Create new chatbot rating
        ChatbotRating chatbotRating = ChatbotRating.builder()
                .user(user)
                .conversationTitle(conversationTitle)
                .messageId(chatbotRatingRequest.getMessageId())
                .message(message)
                .rating(chatbotRatingRequest.getRating())
                .build();
        chatbotRatingRepository.save(chatbotRating);
    }
}
