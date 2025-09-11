package com.hcmute.fit.toeicrise.dtos.responses;

import com.hcmute.fit.toeicrise.models.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private long expirationTime;
    private Long userId;
    private String email;
    private String fullName;
    private ERole role;
}
