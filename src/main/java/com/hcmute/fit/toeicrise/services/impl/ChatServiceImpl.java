package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.responses.ChatbotResponse;
import com.hcmute.fit.toeicrise.dtos.responses.SystemPromptDetailResponse;
import com.hcmute.fit.toeicrise.models.entities.ChatMessage;
import com.hcmute.fit.toeicrise.models.mappers.ChatbotMapper;
import com.hcmute.fit.toeicrise.repositories.ChatMemoryRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IChatService;
import com.hcmute.fit.toeicrise.services.interfaces.ISystemPromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.core.io.InputStreamResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements IChatService {
    private final ChatClient chatClient;
    private final ChatModel chatModel;
    private final ChatMemoryRepository chatMemoryRepository;
    private final ISystemPromptService systemPromptService;
    private final ChatbotMapper chatbotMapper;

    @Override
    public List<ChatbotResponse> getChatHistory(String conversationId) {
        return chatMemoryRepository.getChatHistory(conversationId);
    }

    @Override
    public Flux<ChatbotResponse> chat(String conversationId, String userMessage) {
        Flux<String> content = chatClient.prompt()
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .user(userMessage)
                .system(getActiveSystemPrompt())
                .stream()
                .content();
        String messageId = getLatestAssistantMessageId(conversationId);
        return content.map(contentText -> chatbotMapper.toChatbotResponse(contentText, messageId, conversationId, MessageType.ASSISTANT.name()));
    }

    @Override
    public Flux<ChatbotResponse> chat(String message, String conversationId, InputStream imageInputStream, String contentType) {
        // Save user message first
        Message userMessage = new UserMessage(message);
        chatMemoryRepository.saveMessage(conversationId, userMessage);

        Flux<String> content = ChatClient.create(chatModel)
                .prompt()
                .system(getActiveSystemPrompt())
                .user(user -> user
                        .text(message)
                        .media(MimeTypeUtils.parseMimeType(contentType), new InputStreamResource(imageInputStream)))
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .stream()
                .content();

        // Collect the streaming content and save when complete
        AtomicReference<String> fullResponse = new AtomicReference<>("");

        return content
                .doOnNext(chunk -> {
                    // Accumulate the response
                    fullResponse.updateAndGet(current -> current + chunk);
                })
                .doOnComplete(() -> {
                    // Save the complete assistant message when streaming is done
                    Message assistantMessage = new AssistantMessage(fullResponse.get());
                    chatMemoryRepository.saveMessage(conversationId, assistantMessage);
                })
                .map(contentChunk -> {
                    String messageId = getLatestAssistantMessageId(conversationId);
                    return new ChatbotResponse(contentChunk, messageId, conversationId, MessageType.ASSISTANT.name());
                });
    }

    @Override
    public String generateConversationTitle(String userMessage) {
        String prompt = "Dựa trên tin nhắn sau của người dùng, hãy tạo một tiêu đề ngắn gọn, rõ ràng và phù hợp cho cuộc hội thoại. "
                + "Tiêu đề phải dưới 10 từ, không có dấu ngoặc kép, không thêm giải thích hoặc văn bản thừa. "
                + "Chỉ trả về tiêu đề duy nhất.\n\nTin nhắn người dùng:\n"
                + userMessage;
        return chatClient.prompt()
                .system("Bạn là một trợ lý hữu ích, có nhiệm vụ tạo ra tiêu đề cuộc hội thoại ngắn gọn và phù hợp.")
                .user(prompt)
                .call()
                .content();
    }

    @Async
    @Override
    public void deleteConversation(String conversationId) {
        chatMemoryRepository.deleteByConversationId(conversationId);
    }

    private String getLatestAssistantMessageId(String conversationId) {
        List<Message> allMessages = chatMemoryRepository.findByConversationId(conversationId);
        for (int i = allMessages.size() - 1; i >= 0; i--) {
            Message message = allMessages.get(i);
            if (message instanceof ChatMessage chatMessage &&
                    message.getMessageType() == MessageType.ASSISTANT) {
                return chatMessage.getMessageId();
            }
        }
        return null;
    }

    private String getActiveSystemPrompt() {
        SystemPromptDetailResponse response = systemPromptService.getActiveSystemPrompt();
        return response.getContent();
    }
}
