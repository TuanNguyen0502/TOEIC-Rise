package com.hcmute.fit.toeicrise.dtos.requests;

import lombok.Data;

@Data
public class VerifyUserRequest {
    private String email;
    private String verificationCode;
}
