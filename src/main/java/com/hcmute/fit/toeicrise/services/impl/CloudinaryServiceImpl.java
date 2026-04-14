package com.hcmute.fit.toeicrise.services.impl;

import com.cloudinary.Cloudinary;
import com.hcmute.fit.toeicrise.commons.utils.CloudinaryUtil;
import com.hcmute.fit.toeicrise.dtos.requests.cloudinary.CloudinarySignRequest;
import com.hcmute.fit.toeicrise.dtos.responses.cloudinary.CloudinarySignResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.services.interfaces.ICloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements ICloudinaryService {
    private final CloudinaryUtil cloudinaryUtil;
    private final Cloudinary cloudinary;
    private static final String LESSON_VIDEO_FOLDER = "toeic-rise/lessons";
    private static final List<String> type_sources = List.of(new String[]{"video", "image", "audio"});
    @Value("${cloudinary.api_secret}")
    private String apiSecret;
    @Value("${cloudinary.cloud_name}")
    private String cloudName;
    @Value("${cloudinary.api_key}")
    private String apiKey;

    @Override
    public String processMediaFile(MultipartFile newFile, String newUrl, String oldUrl) {
        boolean hasFile = newFile != null && !newFile.isEmpty();
        boolean hasUrl = newUrl != null && !newUrl.isBlank();

        if (hasFile) {
            if (oldUrl != null && cloudinaryUtil.isCloudinaryUrl(oldUrl))
                return cloudinaryUtil.updateFile(newFile, oldUrl);
            return cloudinaryUtil.uploadFile(newFile);
        }
        if (hasUrl) {
            if (oldUrl != null && cloudinaryUtil.isCloudinaryUrl(oldUrl) && !oldUrl.equals(newUrl)) {
                cloudinaryUtil.deleteFile(oldUrl);
            }
            return newUrl;
        }
        return oldUrl;
    }

    @Override
    public CloudinarySignResponse getSignature(Map<String, Object> paramsToSign, CloudinarySignRequest request) {
        if (request.getSource() == null || request.getSource().isEmpty())
            throw new AppException(ErrorCode.VALIDATION_ERROR, "type of data");
        else if (!type_sources.contains(request.getSource()))
            throw new AppException(ErrorCode.VALIDATION_ERROR, "type of data");

        Long timestamp = request.getTimestamp() / 1000L;
        paramsToSign.put("timestamp", timestamp);
        String signature = cloudinary.apiSignRequest(paramsToSign, apiSecret);
        paramsToSign.put("signature", signature);
        paramsToSign.put("folder", LESSON_VIDEO_FOLDER);

        return CloudinarySignResponse.builder()
                .cloudName(cloudName)
                .signature(signature)
                .apiKey(apiKey)
                .folder(LESSON_VIDEO_FOLDER)
                .timestamp(timestamp)
                .build();
    }
}
