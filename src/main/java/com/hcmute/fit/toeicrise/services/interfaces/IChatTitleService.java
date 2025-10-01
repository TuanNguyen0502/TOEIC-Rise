package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.ChatTitleCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.ChatTitleUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.ChatTitleResponse;

import java.util.List;

public interface IChatTitleService {
    List<ChatTitleResponse> getAllChatTitlesByUserId(String email);

    void createChatTitle(String email, ChatTitleCreateRequest request);

    void renameChatTitle(String email, ChatTitleUpdateRequest request);

    void deleteChatTitle(String email, String conversationId);

    boolean checkConversationIdBelongsToUser(String email, String conversationId);
}
