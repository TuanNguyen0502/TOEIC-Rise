package com.hcmute.fit.toeicrise.dtos.requests.user;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResetPasswordRequest {
    @NotBlank(message = MessageConstant.PASSWORD_NOT_BLANK)
    @NotNull(message = MessageConstant.PASSWORD_NOT_NULL)
    @Pattern(regexp = Constant.PASSWORD_PATTERN, message = MessageConstant.INVALID_PASSWORD)
    private String password;

    @NotBlank(message = MessageConstant.CONFIRM_PASSWORD_NOT_BLANK)
    @NotNull(message = MessageConstant.CONFIRM_PASSWORD_NOT_NULL)
    @Pattern(regexp = Constant.PASSWORD_PATTERN, message = MessageConstant.INVALID_PASSWORD)
    private String confirmPassword;
}
