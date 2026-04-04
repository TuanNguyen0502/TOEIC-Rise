package com.hcmute.fit.toeicrise.validators.constraints;

import com.hcmute.fit.toeicrise.validators.annotations.ValidImage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class ImageFileValidator implements ConstraintValidator<ValidImage, MultipartFile> {
    private static final List<String> CONTENT_TYPES = Arrays.asList("image/png", "image/jpg", "image/jpeg", "image/webp");
    private static final long MAX_FILE_SIZE = 4 * 1024 * 1024; // 4MB

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) return true; // Để @NotNull xử lý nếu cần

        // Kiểm tra định dạng (Content Type)
        if (!CONTENT_TYPES.contains(file.getContentType())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Chỉ chấp nhận ảnh định dạng PNG, JPG hoặc WEBP").addConstraintViolation();
            return false;
        }

        // Kiểm tra dung lượng
        if (file.getSize() > MAX_FILE_SIZE) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Dung lượng ảnh không được vượt quá 2MB").addConstraintViolation();
            return false;
        }

        return true;
    }
}
