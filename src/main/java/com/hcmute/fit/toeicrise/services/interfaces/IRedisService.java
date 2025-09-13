package com.hcmute.fit.toeicrise.services.interfaces;

import java.time.Duration;

public interface IRedisService {
    <T> T get(String cacheName, Object key, Class<T> clazz);
    boolean put(String cacheName, Object key, Object value, Duration cacheDuration);
    boolean remove(String cacheName, Object key);
    String buildKey(String cacheName, Object key);
}