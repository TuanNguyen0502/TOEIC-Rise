package com.hcmute.fit.toeicrise.dtos.requests;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = MessageConstant.EMAIL_NOT_BLANK)
    @NotNull(message = MessageConstant.EMAIL_NOT_NULL)
    @Pattern(regexp = Constant.EMAIL_PATTERN, message = MessageConstant.INVALID_EMAIL)
    private String email;

    @NotBlank(message = MessageConstant.PASSWORD_NOT_BLANK)
    @NotNull(message = MessageConstant.PASSWORD_NOT_NULL)
    @Pattern(regexp = Constant.PASSWORD_PATTERN, message = MessageConstant.INVALID_PASSWORD)
    private String password;
}
