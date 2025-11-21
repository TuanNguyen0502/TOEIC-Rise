package com.hcmute.fit.toeicrise.dtos.responses.user;

import com.hcmute.fit.toeicrise.models.enums.EGender;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileResponse {
    private String email;
    private String fullName;
    private EGender gender;
    private String avatar;
}
