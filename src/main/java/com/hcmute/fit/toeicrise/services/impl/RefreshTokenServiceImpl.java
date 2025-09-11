package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.models.entities.RefreshToken;
import com.hcmute.fit.toeicrise.repositories.AccountRepository;
import com.hcmute.fit.toeicrise.repositories.RefreshTokenRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IRefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements IRefreshTokenService {
    @Value("${security.jwt.refresh-token.expiration}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final AccountRepository accountRepository;

    @Override
    public RefreshToken createRefreshToken(String email) {
        RefreshToken refreshToken = RefreshToken.builder()
                .account(accountRepository.findByEmail(email).get())
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token).orElseThrow();
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired. Please make a new login request");
        }
        return token;
    }
}
