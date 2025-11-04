package com.hcmute.fit.toeicrise.commons.utils;

import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.Optional;

public class CodeGeneratorUtils {

    /**
     * Generates a 6-digit verification code
     * @return A string containing the 6-digit verification code
     */
    public static String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    /**
     * Extract group number from questionGroupId format like "p1_g1", "p1_g2"
     * @param questionGroupId string like "p1_g1"
     * @return group number (1, 2, 3...) or null if invalid format
     */
    public static Optional<Integer> extractGroupNumber(String questionGroupId) {
        if (!StringUtils.hasText(questionGroupId)) {
            return Optional.empty();
        }
        try {
            String[] parts = questionGroupId.split("_g");
            if (parts.length == 2) {
                return Optional.of(Integer.parseInt(parts[1]));
            }
        } catch (NumberFormatException e) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "questionGroupId");
        }
        return Optional.empty();
    }
}
