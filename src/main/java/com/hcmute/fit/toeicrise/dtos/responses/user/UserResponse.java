package com.hcmute.fit.toeicrise.dtos.responses.user;

import com.hcmute.fit.toeicrise.models.enums.ERole;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long userId;
    private String email;
    private Boolean isActive;
    private String fullName;
    private String avatar;
    private ERole role;
    private String updatedAt;
}
