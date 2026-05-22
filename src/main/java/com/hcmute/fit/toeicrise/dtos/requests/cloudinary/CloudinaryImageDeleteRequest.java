package com.hcmute.fit.toeicrise.dtos.requests.cloudinary;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloudinaryImageDeleteRequest {
    @NotBlank(message = MessageConstant.IMAGE_NOT_BLANK)
    private String imageUrl;
}
