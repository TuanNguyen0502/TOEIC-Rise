package com.hcmute.fit.toeicrise.commons.utils;

import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.ChatMessage;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.repositories.ChatMemoryRepository;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;

import java.util.*;

public class ChatMemoryAdvisor implements CallAdvisor, StreamAdvisor {
    private final ChatMemoryRepository chatMemoryRepository;
    private final int chatMemoryRetrieveSize;
    private final String defaultConversationId;
    private final int order;

    public ChatMemoryAdvisor(ChatMemoryRepository repository) {
        this(repository, 20, "default", 0);
    }

    public ChatMemoryAdvisor(ChatMemoryRepository repository,
                             int chatMemoryRetrieveSize,
                             String defaultConversationId,
                             int order) {
        this.chatMemoryRepository = repository;
        this.chatMemoryRetrieveSize = chatMemoryRetrieveSize;
        this.defaultConversationId = defaultConversationId;
        this.order = order;
    }

    @Override
    @NonNull
    public ChatClientResponse adviseCall(@NonNull ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
       PreparedChatRequest prepared = preparedChatRequest(chatClientRequest);
        // Execute the call
        ChatClientResponse response = callAdvisorChain.nextCall(prepared.request);

        // Store assistant response
        storeAssistantResponse(response, prepared.conversationId, prepared.messageId);

        return response;
    }

    @Override
    @NonNull
    public Flux<ChatClientResponse> adviseStream(@NonNull ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        PreparedChatRequest prepared = preparedChatRequest(chatClientRequest);

        // ✨ Stream handling: accumulate assistant response content as it streams in
        StringBuilder assistantContent = new StringBuilder();
        Map<String, Object> metadataHolder = new HashMap<>();

        return streamAdvisorChain.nextStream(prepared.request)
                .doOnNext(response -> {
                    // Append each streamed chunk to the assistant content buffer
                    if (response.chatResponse() != null) {
                        response.chatResponse().getResult();
                        if (response.chatResponse().getResult().getOutput() != null) {

                            String chunk = response.chatResponse().getResult().getOutput().getText();
                            assistantContent.append(chunk);

                            // Save the latest metadata from the current chunk
                            metadataHolder.clear();
                            metadataHolder.putAll(response.chatResponse().getResult().getOutput().getMetadata());
                        }
                    }
                })
                .doOnComplete(() -> {
                    // After the full stream completes, save the full assistant message to the repository
                    if (!assistantContent.isEmpty()) {
                        AssistantMessage assistantMessage = new AssistantMessage(assistantContent.toString(), metadataHolder);
                        ChatMessage chatMessage = new ChatMessage(assistantMessage, prepared.messageId, prepared.conversationId);
                        chatMemoryRepository.saveAll(prepared.conversationId, List.of(chatMessage));
                    }
                });
    }

    private PreparedChatRequest preparedChatRequest(ChatClientRequest chatClientRequest) {
        String conversationId = getConversationId(chatClientRequest);
        String messageId = (String) chatClientRequest.context()
                .getOrDefault("messageId", UUID.randomUUID().toString());
        ChatClientRequest modifiedRequest = addConversationHistory(chatClientRequest, conversationId);
        storeUserMessage(chatClientRequest, conversationId);
        List<Message> transformedMessages = transformedMessages(modifiedRequest);

        // Build a new ChatClientRequest with the transformed messages
        modifiedRequest = ChatClientRequest.builder()
                .prompt(Prompt.builder().messages(transformedMessages).build())
                .context(modifiedRequest.context())
                .build();
        return new PreparedChatRequest(conversationId, messageId, modifiedRequest);
    }

    private List<Message> transformedMessages (ChatClientRequest modifiedRequest) {
        return modifiedRequest.prompt().getInstructions().stream()
                .map(msg -> {
                    if (MessageType.ASSISTANT.equals(msg.getMessageType())) {
                        return new AssistantMessage(msg.getText(), msg.getMetadata());
                    }
                    if (MessageType.USER.equals(msg.getMessageType())) {
                        return UserMessage.builder()
                                .text(msg.getText())
                                .metadata(msg.getMetadata())
                                .build();
                    }
                    if (MessageType.SYSTEM.equals(msg.getMessageType())) {
                        return SystemMessage.builder()
                                .text(msg.getText())
                                .metadata(msg.getMetadata())
                                .build();
                    }
                    return msg;
                }).toList();
    }

    private ChatClientRequest addConversationHistory(ChatClientRequest request, String conversationId) {
        // Get conversation history
        List<Message> allMessages = chatMemoryRepository.findByConversationId(conversationId);
        List<Message> memoryMessages = getRecentMessages(allMessages, chatMemoryRetrieveSize);

        if (memoryMessages.isEmpty()) {
            return request;
        }

        // Add history to existing messages
        List<Message> updatedMessages = new ArrayList<>();
        updatedMessages.addAll(memoryMessages);
        updatedMessages.addAll(request.prompt().getInstructions());

        return request.mutate()
                .prompt(request.prompt().mutate().messages(updatedMessages).build())
                .build();
    }

    private void storeUserMessage(ChatClientRequest request, String conversationId) {
        String userText = Optional.ofNullable(request)
                .map(ChatClientRequest::prompt)
                .map(Prompt::getUserMessage)
                .map(UserMessage::getText)
                .orElse("");
        if (!userText.isEmpty()) {
            UserMessage userMessage = new UserMessage(userText);
            String messageId = UUID.randomUUID().toString();
            ChatMessage ratableUserMessage = new ChatMessage(userMessage, messageId, conversationId);
            chatMemoryRepository.saveAll(conversationId, List.of(ratableUserMessage));
        }
    }

    private void storeAssistantResponse(ChatClientResponse response, String conversationId, String messageId) {
        if (response.chatResponse() == null)
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Chat response");
        if (response.chatResponse().getResult() != null && response.chatResponse().getResult().getOutput() != null) {
            Message assistantMessage = response.chatResponse().getResult().getOutput();
            ChatMessage ratableAssistantMessage = new ChatMessage(assistantMessage, messageId, conversationId);
            chatMemoryRepository.saveAll(conversationId, List.of(ratableAssistantMessage));
        }
    }

    private List<Message> getRecentMessages(List<Message> allMessages, int limit) {
        if (allMessages.size() <= limit) {
            return allMessages;
        }
        return allMessages.subList(allMessages.size() - limit, allMessages.size());
    }

    private String getConversationId(ChatClientRequest request) {
        // Check request parameters for conversation ID
        Object conversationId = request.context().get(ChatMemory.CONVERSATION_ID);
        if (conversationId instanceof String) {
            return (String) conversationId;
        }
        return defaultConversationId;
    }

    private record PreparedChatRequest (
        String conversationId,
        String messageId,
        ChatClientRequest request
    ){}

    public static Builder builder(ChatMemoryRepository repository) {
        return new Builder(repository);
    }

    public static class Builder {
        private final ChatMemoryRepository repository;
        private int chatMemoryRetrieveSize = 20;
        private String defaultConversationId = "default";
        private int order = 0;

        public Builder(ChatMemoryRepository repository) {
            this.repository = repository;
        }

        public Builder chatMemoryRetrieveSize(int size) {
            this.chatMemoryRetrieveSize = size;
            return this;
        }

        public Builder defaultConversationId(String conversationId) {
            this.defaultConversationId = conversationId;
            return this;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public ChatMemoryAdvisor build() {
            return new ChatMemoryAdvisor(
                    repository, chatMemoryRetrieveSize, defaultConversationId, order
            );
        }
    }

    @Override
    @NonNull
    public String getName() {
        return "ChatMemoryAdvisor";
    }

    @Override
    public int getOrder() {
        return order;
    }
}
