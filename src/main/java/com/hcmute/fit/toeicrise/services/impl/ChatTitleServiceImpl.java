package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.ChatTitleCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.ChatTitleUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.ChatTitleResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.ChatTitle;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.ChatTitleMapper;
import com.hcmute.fit.toeicrise.repositories.ChatMemoryRepository;
import com.hcmute.fit.toeicrise.repositories.ChatTitleRepository;
import com.hcmute.fit.toeicrise.repositories.UserRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IChatService;
import com.hcmute.fit.toeicrise.services.interfaces.IChatTitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatTitleServiceImpl implements IChatTitleService {
    private final ChatTitleRepository chatTitleRepository;
    private final ChatMemoryRepository chatMemoryRepository;
    private final UserRepository userRepository;
    private final IChatService chatService;
    private final ChatTitleMapper chatTitleMapper;

    @Override
    public List<ChatTitleResponse> getAllChatTitlesByUserId(String email) {
        // Verify user exists
        User user = userRepository.findByAccount_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User"));
        Long userId = user.getId();
        // Fetch and map chat titles
        return chatTitleRepository.findAllByUser_Id(userId)
                .stream()
                .sorted(Comparator.comparing(ChatTitle::getUpdatedAt))
                .map(chatTitleMapper::toChatTitleResponse)
                .toList();
    }

    @Override
    public void createChatTitle(String email, ChatTitleCreateRequest request) {
        // Verify user exists
        User user = userRepository.findByAccount_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User"));
        // Verify conversation exists
        if (!chatMemoryRepository.existsByConversationId(request.getConversationId())) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Conversation");
        }
        // Create new chat title
        ChatTitle chatTitle = new ChatTitle();
        chatTitle.setId(UUID.randomUUID().toString());
        chatTitle.setUser(user);
        chatTitle.setConversationId(request.getConversationId());
        chatTitle.setTitle(request.getTitle());
        chatTitleRepository.save(chatTitle);
    }

    @Override
    public void renameChatTitle(String email, ChatTitleUpdateRequest request) {
        User user = userRepository.findByAccount_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User"));
        if (!chatTitleRepository.existsByConversationIdAndUser_Id(request.getConversationId(), user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        ChatTitle chatTitle = chatTitleRepository.findByConversationIdAndUser_Id(request.getConversationId(), user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Chat title"));
        chatTitle.setTitle(request.getNewTitle());
        chatTitleRepository.save(chatTitle);
    }

    @Override
    public void deleteChatTitle(String email, String conversationId) {
        User user = userRepository.findByAccount_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User"));
        if (!chatTitleRepository.existsByConversationIdAndUser_Id(conversationId, user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        ChatTitle chatTitle = chatTitleRepository.findByConversationIdAndUser_Id(conversationId, user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Chat title"));
        chatTitleRepository.delete(chatTitle);
        chatService.deleteConversation(conversationId);
    }

    @Override
    public boolean checkConversationIdBelongsToUser(String email, String conversationId) {
        User user = userRepository.findByAccount_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User"));
        Long userId = user.getId();
        return chatTitleRepository.existsByConversationIdAndUser_Id(conversationId, userId);
    }
}
