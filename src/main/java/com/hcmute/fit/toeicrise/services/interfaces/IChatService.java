package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.ChatbotResponse;
import org.springframework.scheduling.annotation.Async;
import reactor.core.publisher.Flux;

import java.io.InputStream;
import java.util.List;

public interface IChatService {
    List<ChatbotResponse> getChatHistory(String conversationId);

    Flux<ChatbotResponse> chat(String conversationId, String userMessage);

    Flux<ChatbotResponse> chat(String message, String conversationId, InputStream imageInputStream, String contentType);

    String generateConversationTitle(String userMessage);

    @Async
    void deleteConversation(String conversationId);
}
