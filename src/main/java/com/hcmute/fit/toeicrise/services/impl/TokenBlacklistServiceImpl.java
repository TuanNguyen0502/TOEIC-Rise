package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.services.interfaces.ITokenBlacklistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistServiceImpl implements ITokenBlacklistService {
    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistServiceImpl.class);
    private static final String BLACKLIST_PREFIX = "blacklist:";
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtServiceImpl jwtService;

    public TokenBlacklistServiceImpl(@Qualifier("tokenBlacklistRedisTemplate") RedisTemplate<String, String> redisTemplate, JwtServiceImpl jwtService) {
        this.redisTemplate = redisTemplate;
        this.jwtService = jwtService;
    }

    /**
     * Blacklist a token by adding it to Redis with an expiration time matching the token's remaining validity
     */
    @Override
    public boolean blacklistToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                logger.warn("Attempted to blacklist null or empty token");
                return false;
            }

            // Extract expiration time from token
            long expirationTime = jwtService.extractClaim(token, claims -> claims.getExpiration().getTime());
            long currentTime = System.currentTimeMillis();
            long ttl = expirationTime - currentTime;

            // Only blacklist if the token is not already expired
            // Token is already expired, so it's effectively blacklisted
            if (ttl > 0) {
                String key = BLACKLIST_PREFIX + token;
                redisTemplate.opsForValue().set(key, "blacklisted", ttl, TimeUnit.MILLISECONDS);

                // Verify the token was actually added to the blacklist
                Boolean exists = redisTemplate.hasKey(key);
                if (!exists) {
                    logger.error("Failed to verify token in blacklist after adding");
                    return false;
                }
            }
            return true;
        } catch (RedisConnectionFailureException e) {
            logger.error("Redis connection failure while blacklisting token", e);
            return false;
        } catch (Exception e) {
            logger.error("Error blacklisting token", e);
            return false;
        }
    }

    /**
     * Check if a token is blacklisted
     */
    @Override
    public boolean isTokenBlacklisted(String token) {
        try {
            if (token == null || token.isEmpty()) {
                logger.warn("Attempted to check null or empty token");
                return false;
            }

            String key = BLACKLIST_PREFIX + token;
            Boolean exists = redisTemplate.hasKey(key);

            if (exists == null) {
                logger.warn("Redis returned null for hasKey check, assuming token is not blacklisted");
                return false;
            }

            logger.debug("Token blacklist check: {}", exists);
            return exists;
        } catch (RedisConnectionFailureException e) {
            logger.error("Redis connection failure while checking blacklisted token", e);
            // If Redis is down, we should deny the token to be safe
            return true;
        } catch (Exception e) {
            logger.error("Error checking if token is blacklisted", e);
            // If there's an error, we should deny the token to be safe
            return true;
        }
    }
}
