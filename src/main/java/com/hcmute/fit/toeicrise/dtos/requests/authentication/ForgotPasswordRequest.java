package com.hcmute.fit.toeicrise.dtos.requests.authentication;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.Email;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordRequest {
    @Email(message = MessageConstant.INVALID_EMAIL)
    private String email;
}