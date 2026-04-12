package com.hcmute.fit.toeicrise.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface ICloudinaryService {
    String processMediaFile(MultipartFile newFile, String newUrl, String oldUrl);
}