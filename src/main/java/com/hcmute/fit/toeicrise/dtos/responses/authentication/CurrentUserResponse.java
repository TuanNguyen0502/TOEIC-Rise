package com.hcmute.fit.toeicrise.dtos.responses.authentication;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrentUserResponse {
    private Long id;
    private String fullName;
    private String avatar;
    private String email;
    private String role;
    private Boolean hasPassword;
}
