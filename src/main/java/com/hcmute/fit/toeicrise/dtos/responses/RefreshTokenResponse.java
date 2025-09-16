package com.hcmute.fit.toeicrise.dtos.responses;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RefreshTokenResponse {
    private String accessToken;
    private long accessTokenExpirationTime;
}
