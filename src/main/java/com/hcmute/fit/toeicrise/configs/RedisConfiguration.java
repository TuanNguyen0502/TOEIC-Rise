package com.hcmute.fit.toeicrise.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;

@Configuration
public class RedisConfiguration {
    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(redisObjectMapper());

        configurationSerializers(template, stringRedisSerializer, serializer);
        template.afterPropertiesSet();
        return template;
    }

    private static void configurationSerializers(RedisTemplate<?, ?> template,
                                                 RedisSerializer<?> keySerializer,
                                                 RedisSerializer<?> valueSerializer) {
        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);
    }

    @Bean
    public RedisTemplate<String, String> tokenBlacklistRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        configurationSerializers(template, stringRedisSerializer, stringRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisSerializationContext.SerializationPair<String> keyPair = RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer());
        RedisSerializationContext.SerializationPair<Object> valuePair = RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json());
        RedisCacheConfiguration systemOverview = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1))
                .serializeKeysWith(keyPair)
                .serializeValuesWith(valuePair);
        return RedisCacheManager.builder(redisConnectionFactory)
                .withCacheConfiguration("systemOverview", systemOverview)
                .build();
    }
}