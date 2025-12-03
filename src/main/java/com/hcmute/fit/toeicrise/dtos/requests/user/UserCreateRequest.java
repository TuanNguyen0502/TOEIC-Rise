package com.hcmute.fit.toeicrise.dtos.requests.user;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.models.enums.EGender;
import com.hcmute.fit.toeicrise.models.enums.ERole;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequest {
    @Email(message = MessageConstant.INVALID_EMAIL)
    @NotBlank(message = MessageConstant.EMAIL_NOT_BLANK)
    private String email;

    @NotBlank(message = MessageConstant.PASSWORD_NOT_BLANK)
    @Pattern(regexp = Constant.PASSWORD_PATTERN, message = MessageConstant.INVALID_PASSWORD)
    private String password;

    @NotBlank(message = MessageConstant.CONFIRM_PASSWORD_NOT_BLANK)
    @Pattern(regexp = Constant.PASSWORD_PATTERN, message = MessageConstant.INVALID_PASSWORD)
    private String confirmPassword;

    @NotBlank(message = MessageConstant.FULLNAME_NOT_BLANK)
    @Pattern(regexp = Constant.FULLNAME_PATTERN, message = MessageConstant.FULLNAME_INVALID)
    private String fullName;

    @NotNull(message = MessageConstant.GENDER_NOT_NULL)
    private EGender gender;

    private MultipartFile avatar;

    @NotNull(message = MessageConstant.ROLE_NOT_NULL)
    private ERole role;
}
