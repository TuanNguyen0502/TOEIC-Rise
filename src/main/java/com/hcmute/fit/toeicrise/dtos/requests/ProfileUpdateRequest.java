package com.hcmute.fit.toeicrise.dtos.requests;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.models.enums.EGender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class ProfileUpdateRequest {
    @NotNull(message = MessageConstant.PROFILE_FULLNAME_NOT_NULL)
    @NotBlank(message = MessageConstant.PROFILE_FULLNAME_NOT_BLANK)
    @Pattern(regexp = Constant.PROFILE_FULLNAME_PATTERN, message = MessageConstant.PROFILE_FULLNAME_INVALID)
    private String fullName;

    @NotNull(message = MessageConstant.PROFILE_GENDER_NOT_NULL)
    private EGender gender;

    private MultipartFile avatar;
}
