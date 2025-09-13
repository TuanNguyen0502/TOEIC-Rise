package com.hcmute.fit.toeicrise.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.services.interfaces.IRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements IRedisService {
    private final RedisTemplate<Object, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public <T> T get(String cacheName, Object key, Class<T> clazz) {
        String json = (String) redisTemplate.opsForValue().get(buildKey(cacheName, key));
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        }
        catch (Exception e) {
            throw new AppException(ErrorCode.CACHE_ERROR);
        }
    }

    @Override
    public boolean put(String cacheName, Object key, Object value, Duration cacheDuration) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(buildKey(cacheName, key), json, cacheDuration);
            return true;
        } catch (Exception e) {
            throw new AppException(ErrorCode.CACHE_ERROR);
        }
    }

    @Override
    public boolean remove(String cacheName, Object key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(buildKey(cacheName, key)));
    }

    @Override
    public String buildKey(String cacheName, Object key) {
        return cacheName + "::" + key;
    }
}