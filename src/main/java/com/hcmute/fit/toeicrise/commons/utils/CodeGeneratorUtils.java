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

    /**
     * Kiểm tra xem một giá trị String có hợp lệ với enum hay không
     * @param enumClass class của enum (ví dụ: ETestSetStatus.class)
     * @param value giá trị cần kiểm tra
     * @param <E> kiểu enum
     * @return true nếu tồn tại, false nếu không
     */
    public static <E extends Enum<E>> boolean isValidEnum(Class<E> enumClass, String value) {
        if (value == null) return false;
        for (E e : enumClass.getEnumConstants()) {
            if (e.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
