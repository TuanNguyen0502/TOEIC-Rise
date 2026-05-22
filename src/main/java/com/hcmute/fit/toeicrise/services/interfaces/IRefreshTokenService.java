package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.authentication.RefreshTokenResponse;

public interface IRefreshTokenService {
    RefreshTokenResponse getRefreshToken(String refreshToken);
    String createRefreshTokenWithRefreshToken(String refreshToken);
    String createRefreshTokenWithEmail(String email);
    long getRefreshTokenDurationMs();
}
