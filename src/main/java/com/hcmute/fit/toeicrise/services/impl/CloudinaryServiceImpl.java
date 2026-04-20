package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.utils.CloudinaryUtil;
import com.hcmute.fit.toeicrise.services.interfaces.ICloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements ICloudinaryService {
    private final CloudinaryUtil cloudinaryUtil;

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
}
