package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.ChatTitleCreateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.ChatTitleResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.ChatTitle;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.ChatTitleMapper;
import com.hcmute.fit.toeicrise.repositories.ChatTitleRepository;
import com.hcmute.fit.toeicrise.repositories.UserRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IChatService;
import com.hcmute.fit.toeicrise.services.interfaces.IChatTitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatTitleServiceImpl implements IChatTitleService {
    private final ChatTitleRepository chatTitleRepository;
    private final UserRepository userRepository;
    private final IChatService chatService;
    private final ChatTitleMapper chatTitleMapper;

    @Override
    public List<ChatTitleResponse> getAllChatTitlesByUserId(Long userId) {
        return chatTitleRepository.findAllByUser_Id(userId)
                .stream()
                .sorted(Comparator.comparing(ChatTitle::getUpdatedAt))
                .map(chatTitleMapper::toChatTitleResponse)
                .toList();
    }

    @Override
    public void createChatTitle(ChatTitleCreateRequest request) {
        // Verify user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User"));
        // Create new chat title
        ChatTitle chatTitle = new ChatTitle();
        chatTitle.setUser(user);
        chatTitle.setConversationId(request.getConversationId());
        chatTitle.setTitle(request.getTitle());
        chatTitleRepository.save(chatTitle);
    }

    @Override
    public void renameChatTitle(Long chatTitleId, String newTitle) {
        ChatTitle chatTitle = chatTitleRepository.findById(chatTitleId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Chat title"));
        chatTitle.setTitle(newTitle);
        chatTitleRepository.save(chatTitle);
    }

    @Override
    public void deleteChatTitle(Long chatTitleId) {
        ChatTitle chatTitle = chatTitleRepository.findById(chatTitleId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Chat title"));
        String conversationId = chatTitle.getConversationId();
        chatTitleRepository.delete(chatTitle);
        chatService.deleteConversation(conversationId);
    }
}
