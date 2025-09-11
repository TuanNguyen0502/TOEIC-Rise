package com.hcmute.fit.toeicrise.dtos.requests;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequest{
    @NotBlank(message = "Please enter your email!")
    @Pattern(regexp = Constant.EMAIL_PATTERN, message = MessageConstant.INVALID_EMAIL)
    private String email;

    @NotBlank(message = "Please enter your password!")
    @Pattern(regexp = Constant.PASSWORD_PATTERN, message = MessageConstant.INVALID_PASSWORD)
    private String password;
}
