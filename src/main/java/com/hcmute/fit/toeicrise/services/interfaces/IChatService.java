package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.chatbot.ChatAboutQuestionRequest;
import com.hcmute.fit.toeicrise.dtos.requests.chatbot.ChatRequest;
import com.hcmute.fit.toeicrise.dtos.requests.chatbot.TitleRequest;
import com.hcmute.fit.toeicrise.dtos.responses.chatbot.ChatbotResponse;
import reactor.core.publisher.Flux;

import java.io.InputStream;
import java.util.List;

public interface IChatService {
    List<ChatbotResponse> getChatHistory(String conversationId);

    Flux<ChatbotResponse> chat(ChatRequest chatRequest);

    Flux<ChatbotResponse> chat(ChatRequest chatRequest, InputStream imageInputStream, String contentType);

    String generateConversationTitle(String email, TitleRequest titleRequest);

    Flux<ChatbotResponse> chatAboutQuestion(ChatAboutQuestionRequest chatAboutQuestionRequest);

    String recommendTests(String userQuery);
}
