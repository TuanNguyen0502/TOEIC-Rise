package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.cloudinary.CloudinarySignRequest;
import com.hcmute.fit.toeicrise.dtos.responses.cloudinary.CloudinarySignResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ICloudinaryService {
    String processMediaFile(MultipartFile newFile, String newUrl, String oldUrl);
    CloudinarySignResponse getSignature(Map<String, Object> paramsToSign, CloudinarySignRequest cloudinarySignRequest);
}