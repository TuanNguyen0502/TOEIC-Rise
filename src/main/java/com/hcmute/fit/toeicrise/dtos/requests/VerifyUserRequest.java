package com.hcmute.fit.toeicrise.dtos.requests;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class VerifyUserRequest {
    @Email(message = MessageConstant.INVALID_EMAIL)
    private String email;
    private String verificationCode;
}
