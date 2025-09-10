package com.hcmute.fit.toeicrise.dtos.requests;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String fullName;
}
