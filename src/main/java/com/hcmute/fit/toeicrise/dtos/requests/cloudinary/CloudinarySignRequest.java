package com.hcmute.fit.toeicrise.dtos.requests.cloudinary;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloudinarySignRequest {
    @NotNull(message = MessageConstant.CLOUDINARY_TIMESTAMP_NOT_NULL)
    private Long timestamp;

    @NotBlank(message = MessageConstant.CLOUDINARY_SOURCE_NOT_BLANK)
    @NotNull(message = MessageConstant.CLOUDINARY_SOURCE_NOT_NULL)
    private String source;
}
