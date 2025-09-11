package com.hcmute.fit.toeicrise.dtos.requests;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyUserRequest {
    @Pattern(regexp = Constant.EMAIL_PATTERN, message = MessageConstant.INVALID_EMAIL)
    private String email;
    private String verificationCode;
}
