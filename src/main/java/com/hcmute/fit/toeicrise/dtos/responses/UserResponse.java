package com.hcmute.fit.toeicrise.dtos.responses;

import com.hcmute.fit.toeicrise.models.entities.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long userId;
    private String email;
    private Boolean isActive;
    private String fullName;
    private String avatar;
    private Role role;
    private String updatedAt;
}
