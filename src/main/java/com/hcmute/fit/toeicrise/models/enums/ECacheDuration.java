package com.hcmute.fit.toeicrise.models.enums;

import lombok.Getter;

import java.time.Duration;

@Getter
public enum ECacheDuration {
    CACHE_REGISTRATION("cacheRegistration", Duration.ofHours(12)),
    CACHE_FULLNAME_REGISTRATION("cacheRegistrationFullName", Duration.ofHours(12)),
    CACHE_LIMIT_VERIFY_OTP("cacheLimitVerifyOtp", Duration.ofHours(12)),;

    private final String cacheName;
    private final Duration duration;

    ECacheDuration(String cacheName, Duration duration) {
        this.cacheName = cacheName;
        this.duration = duration;
    }
}