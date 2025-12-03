package com.hcmute.fit.toeicrise.dtos.responses.user;

import com.hcmute.fit.toeicrise.models.enums.EGender;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
    private String email;
    private String fullName;
    private EGender gender;
    private String avatar;
}
