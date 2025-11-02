package com.hcmute.fit.toeicrise.dtos.requests.user;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.models.enums.EGender;
import com.hcmute.fit.toeicrise.models.enums.ERole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class UserUpdateRequest {
    @NotNull(message = MessageConstant.FULLNAME_NOT_NULL)
    @NotBlank(message = MessageConstant.FULLNAME_NOT_BLANK)
    @Pattern(regexp = Constant.FULLNAME_PATTERN, message = MessageConstant.FULLNAME_INVALID)
    private String fullName;

    @NotNull(message = MessageConstant.GENDER_NOT_NULL)
    private EGender gender;

    private MultipartFile avatar;

    @NotNull(message = MessageConstant.ROLE_NOT_NULL)
    private ERole role;

    @NotNull(message = MessageConstant.IS_ACTIVE_NOT_NULL)
    private boolean isActive;
}
