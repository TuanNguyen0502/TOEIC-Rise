package com.hcmute.fit.toeicrise.configs;

import com.hcmute.fit.toeicrise.commons.utils.ChatMemoryAdvisor;
import com.hcmute.fit.toeicrise.repositories.ChatMemoryRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class ChatConfiguration {
    @Bean
    @Primary
    public ChatMemoryRepository chatMemoryRepository(JdbcTemplate jdbcTemplate) {
        return new ChatMemoryRepository(jdbcTemplate);
    }

    @Bean
    public ChatMemoryAdvisor ratingChatMemoryAdvisor(ChatMemoryRepository repository) {
        return ChatMemoryAdvisor.builder(repository)
                .chatMemoryRetrieveSize(100)
                .defaultConversationId("default")
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ChatMemoryAdvisor advisor) {
        return builder
                .defaultAdvisors(advisor)
                .build();
    }
}
