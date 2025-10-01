package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.responses.ChatTitleResponse;
import com.hcmute.fit.toeicrise.models.mappers.ChatTitleMapper;
import com.hcmute.fit.toeicrise.repositories.ChatTitleRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IChatTitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatTitleServiceImpl implements IChatTitleService {
    private final ChatTitleRepository chatTitleRepository;
    private final ChatTitleMapper chatTitleMapper;

    public List<ChatTitleResponse> getAllChatTitlesByUserId(Long userId) {
        return chatTitleRepository.findAllByUser_Id(userId)
                .stream()
                .map(chatTitleMapper::toChatTitleResponse)
                .toList();
    }
}
