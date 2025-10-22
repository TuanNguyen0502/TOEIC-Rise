package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.ChatRequest;
import com.hcmute.fit.toeicrise.dtos.requests.TitleRequest;
import com.hcmute.fit.toeicrise.dtos.responses.ChatbotResponse;
import org.springframework.scheduling.annotation.Async;
import reactor.core.publisher.Flux;

import java.io.InputStream;
import java.util.List;

public interface IChatService {
    List<ChatbotResponse> getChatHistory(String conversationId);

    Flux<ChatbotResponse> chat(ChatRequest chatRequest);

    Flux<ChatbotResponse> chat(ChatRequest chatRequest, InputStream imageInputStream, String contentType);

    String generateConversationTitle(String email, TitleRequest titleRequest);

    @Async
    void deleteConversation(String conversationId);
}
