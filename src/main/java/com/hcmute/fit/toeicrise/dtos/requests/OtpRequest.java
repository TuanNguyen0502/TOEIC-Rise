package com.hcmute.fit.toeicrise.dtos.requests;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpRequest {
    @Email(message = MessageConstant.INVALID_EMAIL)
    private String email;
    @Pattern(regexp = Constant.OTP_PATTERN, message = MessageConstant.INVALID_OTP)
    private String otp;
}