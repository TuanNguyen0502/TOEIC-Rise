package com.hcmute.fit.toeicrise.commons.utils;

import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import org.springframework.util.StringUtils;

import java.util.Random;

public class CodeGeneratorUtils {

    /**
     * Generates a 6-digit verification code
     * @return A string containing the 6-digit verification code
     */
    public static String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    /**
     * Extract group number from questionGroupId format like "p1_g1", "p1_g2"
     * @param questionGroupId string like "p1_g1"
     * @return group number (1, 2, 3...) or null if invalid format
     */
    public static Integer extractGroupNumber(String questionGroupId) {
        if (!StringUtils.hasText(questionGroupId)) {
            return null;
        }
        try {
            String[] parts = questionGroupId.split("_g");
            if (parts.length == 2) {
                return Integer.parseInt(parts[1]);
            }
        } catch (NumberFormatException e) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "questionGroupId");
        }
        return null;
    }
}
