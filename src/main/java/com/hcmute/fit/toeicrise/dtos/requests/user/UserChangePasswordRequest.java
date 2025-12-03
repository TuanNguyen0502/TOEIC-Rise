package com.hcmute.fit.toeicrise.dtos.requests.user;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserChangePasswordRequest {
    @NotBlank(message = MessageConstant.PASSWORD_NOT_BLANK)
    private String oldPassword;

    @Pattern(regexp = Constant.PASSWORD_PATTERN, message = MessageConstant.INVALID_PASSWORD)
    private String newPassword;

    @NotBlank(message = MessageConstant.CONFIRM_PASSWORD_NOT_BLANK)
    private String confirmPassword;
}