package com.hcmute.fit.toeicrise.commons.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

@Component
public class CloudinaryUtil {
    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryUtil(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String getDefaultAvatarUrl() {
        return "https://res.cloudinary.com/toeic-rise/image/upload/v1761193814/default-avatar_hbm1bj.png";
    }

    @SuppressWarnings("unchecked")
    public String uploadFile(MultipartFile file) {
        try {
            Map<String, Object> data = cloudinary.uploader()
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
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "auto"));
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

    private boolean isValidSuffixImage(String img) {
        String lower = img.toLowerCase(Locale.ROOT);
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                lower.endsWith(".png") || lower.endsWith(".gif") ||
                lower.endsWith(".bmp") || lower.endsWith(".webp");
    }

    private boolean isValidSuffixAudio(String audio) {
        String lower = audio.toLowerCase(Locale.ROOT);
        return lower.endsWith(".mp3") || lower.endsWith(".wav") ||
                lower.endsWith(".aac") || lower.endsWith(".flac") ||
                lower.endsWith(".ogg") || lower.endsWith(".m4a");
    }

    public boolean isCloudinaryUrl(String url) {
        return url.contains("res.cloudinary.com");
    }

    private String extractPublicId(String url) {
        if (url == null || url.isEmpty()) return null;
        // URL: https://res.cloudinary.com/your_cloud/image/upload/v1234567890/filename.jpg

        int uploadIndex = url.indexOf("/upload/");
        if (uploadIndex == -1) return null;

        String after = url.substring(uploadIndex + 8);
        String cleaned = after.replaceAll("^v\\d+/","");

        return cleaned.replaceFirst("\\.[^.]+$","");
    }
}
