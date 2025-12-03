package com.hcmute.fit.toeicrise.dtos.responses.authentication;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenResponse {
    private String accessToken;
    private long accessTokenExpirationTime;
}
