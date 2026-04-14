package com.hcmute.fit.toeicrise.dtos.responses.cloudinary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloudinarySignResponse {
    private String cloudName;
    private String apiKey;
    private Long timestamp;
    private String signature;
    private String folder;
}
