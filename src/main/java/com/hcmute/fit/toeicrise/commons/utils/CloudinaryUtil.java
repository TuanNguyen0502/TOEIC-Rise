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

    public String updateFile(MultipartFile file, String oldUrl) {
        deleteFile(oldUrl);
        return uploadFile(file);
    }

    public void deleteFile(String url) {
        try {
            String publicId = extractPublicId(url);
            String resourceType = getResourceTypeFromUrl(url);
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", resourceType));
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_DELETE_FAILED);
        }
    }

    public void validateImageFile(MultipartFile image) {
        String filename = image.getOriginalFilename();
        if (filename == null || !isValidSuffixImage(filename)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Invalid image file format.");
        }
    }

    public void validateImageURL(String imageUrl) {
        if (!isValidSuffixImage(imageUrl)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Invalid image file format.");
        }
    }

    public void validateAudioFile(MultipartFile audio) {
        String filename = audio.getOriginalFilename();
        if (filename == null || !isValidSuffixAudio(filename)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Invalid audio file format.");
        }
    }

    public void validateAudioURL(String audioUrl) {
        if (!isValidSuffixAudio(audioUrl)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Invalid audio file format.");
        }
    }

    public void handleUploadFile(MultipartFile file, int maxSize) {
        if (file != null && !file.isEmpty()) {
            if (file.getSize() > maxSize) {
                throw new AppException(ErrorCode.IMAGE_SIZE_EXCEEDED);
            }
            validateImageFile(file);
        }
    }

    private boolean isValidSuffixImage(String img) {
        return img.endsWith(".jpg") || img.endsWith(".jpeg") ||
                img.endsWith(".png") || img.endsWith(".gif") ||
                img.endsWith(".bmp") || img.endsWith(".webp");
    }

    private boolean isValidSuffixAudio(String audio) {
        return audio.endsWith(".mp3") || audio.endsWith(".wav") ||
                audio.endsWith(".aac") || audio.endsWith(".flac") ||
                audio.endsWith(".ogg") || audio.endsWith(".m4a");
    }

    public boolean isCloudinaryUrl(String url) {
        return url.contains("res.cloudinary.com");
    }

    private String getResourceType(String filename) {
        if (filename == null) return "raw";
        if (isValidSuffixImage(filename)) return "image";
        if (isValidSuffixAudio(filename)) return "video"; // Audio files use 'video' resource type
        return "raw";
    }

    private String getResourceTypeFromUrl(String url) {
        if (url == null) return "raw";
        if (url.contains("/image/")) return "image";
        if (url.contains("/video/")) return "video";
        if (url.contains("/raw/")) return "raw";
        // Fallback based on file extension
        return getResourceType(url);
    }

    private String extractPublicId(String url) {
        if (url == null || url.isEmpty()) return null;
        // URL: https://res.cloudinary.com/your_cloud/image/upload/v1234567890/filename.jpg

        String[] parts = url.split("/");
        if (parts.length < 2) return null; // Invalid URL

        String filename = parts[parts.length - 1]; // filename.jpg
        // Remove file extension
        return filename.contains(".") ? filename.split("\\.")[0] : filename;
    }
}
