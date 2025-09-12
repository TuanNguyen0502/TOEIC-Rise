package com.hcmute.fit.toeicrise.commons.utils;

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
}
