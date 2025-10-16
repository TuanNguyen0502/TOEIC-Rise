package com.hcmute.fit.toeicrise.commons.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Component
public class CloudinaryUtil {
    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryUtil(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadFile(MultipartFile file) {
        try {
            Map data = cloudinary.uploader()
                    .upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
            // Return the URL of the uploaded file
            return data.get("secure_url").toString();
        } catch (IOException e) {
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }
    }

    public void deleteFile(String url) {
        try {
            String publicId = extractPublicId(url);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_DELETE_FAILED);
        }
    }

    public boolean isValidSuffixImage(String img) {
        return img.endsWith(".jpg") || img.endsWith(".jpeg") ||
                img.endsWith(".png") || img.endsWith(".gif") ||
                img.endsWith(".bmp");
    }

    public boolean isValidSuffixAudio(String audio) {
        return audio.endsWith(".mp3") || audio.endsWith(".wav") ||
                audio.endsWith(".aac") || audio.endsWith(".flac") ||
                audio.endsWith(".ogg");
    }

    public String updateFile(MultipartFile file, String oldUrl) {
        deleteFile(oldUrl);
        return uploadFile(file);
    }

    private String extractPublicId(String url) {
        if (url == null || url.isEmpty()) return null;
        // URL: https://res.cloudinary.com/your_cloud/image/upload/v1234567890/filename.jpg
        String[] parts = url.split("/");
        String filename = parts[parts.length - 1]; // filename.jpg
        return filename.split("\\.")[0]; // filename
    }
}
