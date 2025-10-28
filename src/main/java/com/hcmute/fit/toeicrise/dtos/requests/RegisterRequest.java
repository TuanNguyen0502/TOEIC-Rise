package com.hcmute.fit.toeicrise.dtos.requests;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = MessageConstant.EMAIL_NOT_BLANK)
    @Email(message = MessageConstant.INVALID_EMAIL)
    private String email;

    @NotBlank(message = MessageConstant.PASSWORD_NOT_BLANK)
    @Pattern(regexp = Constant.PASSWORD_PATTERN, message = MessageConstant.INVALID_PASSWORD)
    private String password;

    @NotBlank(message = MessageConstant.CONFIRM_PASSWORD_NOT_BLANK)
    private String confirmPassword;

    @NotBlank(message = "Please enter your full name!")
    @Pattern(regexp = Constant.FULLNAME_PATTERN, message = MessageConstant.FULLNAME_INVALID)
    private String fullName;
}
