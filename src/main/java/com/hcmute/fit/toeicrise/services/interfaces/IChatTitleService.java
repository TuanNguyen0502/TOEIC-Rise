package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.chatbot.ChatTitleUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.chatbot.ChatTitleResponse;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface IChatTitleService {
    List<ChatTitleResponse> getAllChatTitlesByUserId(String email);

    @Async
    void createChatTitle(String email, String conversationId, String newTitle);

    void renameChatTitle(String email, ChatTitleUpdateRequest request);

    void deleteChatTitle(String email, String conversationId);

    void checkConversationIdBelongsToUser(String email, String conversationId);
}
