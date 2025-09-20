package com.hcmute.fit.toeicrise.services.interfaces;

public interface ITokenBlacklistService {
    boolean blacklistToken(String token);

    boolean isTokenBlacklisted(String token);
}
