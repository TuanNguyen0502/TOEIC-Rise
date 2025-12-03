package com.hcmute.fit.toeicrise.dtos.responses.authentication;

import com.hcmute.fit.toeicrise.models.enums.ERole;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String accessToken;
    private long expirationTime;
    private Long userId;
    private String email;
    private String fullName;
    private ERole role;
}
