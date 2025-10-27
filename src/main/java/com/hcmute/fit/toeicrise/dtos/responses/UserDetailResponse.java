package com.hcmute.fit.toeicrise.dtos.responses;

import com.hcmute.fit.toeicrise.models.enums.EAuthProvider;
import com.hcmute.fit.toeicrise.models.enums.EGender;
import com.hcmute.fit.toeicrise.models.enums.ERole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDetailResponse {
    private Long userId;
    private String email;
    private EAuthProvider authProvider;
    private Boolean isActive;
    private String fullName;
    private EGender gender;
    private String avatar;
    private ERole role;
    private String createdAt;
    private String updatedAt;
}
