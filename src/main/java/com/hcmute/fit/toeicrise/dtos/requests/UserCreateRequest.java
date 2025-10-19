package com.hcmute.fit.toeicrise.dtos.requests;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.models.enums.EGender;
import com.hcmute.fit.toeicrise.models.enums.ERole;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class UserCreateRequest {
    @Pattern(regexp = Constant.EMAIL_PATTERN, message = MessageConstant.INVALID_EMAIL)
    @NotNull(message = MessageConstant.EMAIL_NOT_NULL)
    @NotBlank(message = MessageConstant.EMAIL_NOT_BLANK)
    private String email;

    @NotBlank(message = MessageConstant.PASSWORD_NOT_BLANK)
    @NotNull(message = MessageConstant.PASSWORD_NOT_NULL)
    @Pattern(regexp = Constant.PASSWORD_PATTERN, message = MessageConstant.INVALID_PASSWORD)
    private String password;

    @NotBlank(message = MessageConstant.CONFIRM_PASSWORD_NOT_BLANK)
    @NotNull(message = MessageConstant.CONFIRM_PASSWORD_NOT_NULL)
    @Pattern(regexp = Constant.PASSWORD_PATTERN, message = MessageConstant.INVALID_PASSWORD)
    private String confirmPassword;

    @NotNull(message = MessageConstant.FULLNAME_NOT_NULL)
    @NotBlank(message = MessageConstant.FULLNAME_NOT_BLANK)
    @Pattern(regexp = Constant.FULLNAME_PATTERN, message = MessageConstant.FULLNAME_INVALID)
    private String fullName;

    @NotNull(message = MessageConstant.GENDER_NOT_NULL)
    private EGender gender;

    @Size(max = Constant.AVATAR_MAX_SIZE, message = MessageConstant.AVATAR_INVALID_SIZE)
    private MultipartFile avatar;

    @NotNull(message = MessageConstant.ROLE_NOT_NULL)
    private ERole role;
}
