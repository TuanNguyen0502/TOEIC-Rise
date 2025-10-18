package com.hcmute.fit.toeicrise.dtos.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileResponse {
    private Long userId;
    private String email;
    private String fullName;
    private String gender;
    private String avatar;
}
