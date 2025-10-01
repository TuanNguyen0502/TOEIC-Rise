package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.ChatTitleResponse;

import java.util.List;

public interface IChatTitleService {
    List<ChatTitleResponse> getAllChatTitlesByUserId(Long userId);

    void renameChatTitle(Long chatTitleId, String newTitle);

    void deleteChatTitle(Long chatTitleId);
}
