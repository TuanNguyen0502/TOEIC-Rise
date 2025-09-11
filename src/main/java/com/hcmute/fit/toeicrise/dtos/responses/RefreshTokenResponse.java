package com.hcmute.fit.toeicrise.dtos.responses;

import lombok.Builder;

@Builder
public class RefreshTokenResponse {
    private String accessToken;
    private String refreshToken;
    private long expirationTime;
}
