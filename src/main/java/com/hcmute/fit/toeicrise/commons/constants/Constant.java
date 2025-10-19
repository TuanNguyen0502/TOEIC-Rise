package com.hcmute.fit.toeicrise.commons.constants;

import java.time.Duration;

public class Constant {
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String TIMEZONE_VIETNAM = "Asia/Ho_Chi_Minh";
    public static final String EMAIL_PATTERN = "(?i)[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}";
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[.@#$%^&+=])(?=\\S+$).{8,20}$";
    public static final String OTP_PATTERN = "^\\d{6}$";
    public static final String NAME_TEST_SET_PATTERN = "^[a-zA-Z0-9 ()]+$";
    public static final String TEST_NAME_PATTERN = "^[a-zA-Z0-9 ()]+$";
    public static final String SYSTEM_PROMPT_CONTENT_PATTERN = "^[a-zA-Z0-9 .,!?()'\"-]{20,}$";
    // Cache constants
    public static final String SYSTEM_PROMPT_CACHE = "systemPrompt";
    public static final String ACTIVE_PROMPT_KEY = "active";
    public static final Duration CACHE_DURATION = Duration.ofDays(30);
    // User constants
    public static final String FULLNAME_PATTERN = "^[a-zA-Z ]+$";
    public static final int AVATAR_MAX_SIZE = 2 * 1024 * 1024; // 2MB
}