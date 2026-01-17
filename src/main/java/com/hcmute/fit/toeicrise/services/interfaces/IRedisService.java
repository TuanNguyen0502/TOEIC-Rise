package com.hcmute.fit.toeicrise.services.interfaces;

import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.function.Consumer;

public interface IRedisService {
    <T> T get(String cacheName, Object key, Class<T> clazz);
    boolean put(String cacheName, Object key, Object value, Duration cacheDuration);
    boolean remove(String cacheName, Object key);
    String buildKey(String cacheName, Object key);
    void batch(Consumer<RedisTemplate<Object, Object>> action);
}