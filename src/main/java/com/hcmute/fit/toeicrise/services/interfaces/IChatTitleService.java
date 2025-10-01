package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.ChatTitleCreateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.ChatTitleResponse;

import java.util.List;

public interface IChatTitleService {
    List<ChatTitleResponse> getAllChatTitlesByUserId(String email);

    void createChatTitle(String email, ChatTitleCreateRequest request);

    void renameChatTitle(Long chatTitleId, String newTitle);

    void deleteChatTitle(Long chatTitleId);
}
