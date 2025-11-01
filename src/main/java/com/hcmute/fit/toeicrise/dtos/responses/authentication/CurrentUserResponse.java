package com.hcmute.fit.toeicrise.dtos.responses.authentication;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrentUserResponse {
    private Long id;
    private String fullName;
    private String avatar;
    private String email;
    private String role;
    private Boolean hasPassword;
}
