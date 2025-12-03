package com.hcmute.fit.toeicrise.dtos.requests.authentication;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = MessageConstant.EMAIL_NOT_BLANK)
    @Email(message = MessageConstant.INVALID_EMAIL)
    private String email;

    @NotBlank(message = MessageConstant.PASSWORD_NOT_BLANK)
    @Pattern(regexp = Constant.PASSWORD_PATTERN, message = MessageConstant.INVALID_PASSWORD)
    private String password;
}
