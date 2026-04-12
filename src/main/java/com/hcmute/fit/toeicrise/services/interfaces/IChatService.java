package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.chatbot.*;
import com.hcmute.fit.toeicrise.dtos.requests.flashcard.SentenceCreateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.chatbot.ChatbotAnalysisResponse;
import com.hcmute.fit.toeicrise.dtos.responses.chatbot.ChatbotResponse;
import reactor.core.publisher.Flux;

import java.io.InputStream;
import java.util.List;

public interface IChatService {
    List<ChatbotResponse> getChatHistory(String conversationId);

    Flux<ChatbotResponse> chat(ChatRequest chatRequest);

    Flux<ChatbotResponse> chat(TestingSystemPromptChatbotRequest request);

    Flux<ChatbotResponse> chat(ChatRequest chatRequest, InputStream imageInputStream, String contentType);

    String generateConversationTitle(String email, TitleRequest titleRequest);

    Flux<ChatbotResponse> chatAboutQuestion(ChatAboutQuestionRequest chatAboutQuestionRequest);

    Flux<ChatbotResponse> chatAboutQuestion(TestingSystemPromptQAndAnswerRequest request);

    ChatbotAnalysisResponse chatAnalysisData(ChatAnalysisRequest chatRequest);

    Flux<ChatbotResponse> generateExplanation(GenerateExplanationRequest request);

    Flux<ChatbotResponse> generateExplanation(TestingSystemPromptExplanationGenerationRequest request);

    Flux<ChatbotResponse> generateBlogPostSummary(BlogPostSummaryRequest request);

    String generateFeedbackForWritingTestAnswerWithImage(String answerText, String partName, String passage, InputStream imageInputStream, String contentType);

    String generateFeedbackForWritingTestAnswerWithoutImage(String answerText, String partName, String passage);

    String generateFeedbackForSpeakingTestAnswerWithImage(String partName, String passage, String questionContent, InputStream imageInputStream, String imageContentType, InputStream audioInputStream, String audioContentType);

    String generateFeedbackForSpeakingTestAnswerWithoutImage(String partName, String passage, String questionContent, InputStream audioInputStream, String audioContentType);

    Flux<ChatbotResponse> chatAboutSentenceStream(SentenceCreateRequest sentenceCreateRequest);

}
