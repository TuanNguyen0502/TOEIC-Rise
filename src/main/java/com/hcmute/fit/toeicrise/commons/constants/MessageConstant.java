package com.hcmute.fit.toeicrise.commons.constants;

public class MessageConstant {
    public static final String INVALID_EMAIL = "Invalid email! Please enter a valid email address.";
    public static final String INVALID_PASSWORD = "Invalid password! Password must be 8-20 characters long, contain at least one digit, one lowercase letter, one uppercase letter, one special character from .@#$%^&+=, and have no whitespace.";
    public static final String INVALID_OTP = "Invalid OTP! OTP must be exactly 6 digits (0-9).";
    public static final String INVALID_TEST_SET = "Test set name can only contain letters, digits, spaces, and parentheses.";
    // System Prompt related messages
    public static final String SYSTEM_PROMPT_CONTENT_NOT_BLANK = "Content must not be blank";
    public static final String SYSTEM_PROMPT_CONTENT_INVALID = "Content must be at least 20 characters and can only contain letters, digits, spaces, and punctuation (.,!?()'\"-)";
    public static final String SYSTEM_PROMPT_CONTENT_NOT_NULL = "Content must not be null";
    public static final String SYSTEM_PROMPT_IS_ACTIVE_NOT_NULL = "isActive must not be null";
    // Test related messages
    public static final String TEST_NAME_INVALID = "Test's name can only contain letters, digits, spaces, and parentheses.";
    public static final String TEST_NAME_NOT_BLANK = "Test's name must not be blank.";
    public static final String TEST_STATUS_NOT_NULL = "Test's status must not be null.";
    // User related messages
    public static final String EMAIL_NOT_NULL = "Email must not be null.";
    public static final String EMAIL_NOT_BLANK = "Email must not be blank.";
    public static final String PASSWORD_NOT_NULL = "Password must not be null.";
    public static final String PASSWORD_NOT_BLANK = "Password must not be blank.";
    public static final String CONFIRM_PASSWORD_NOT_NULL = "Confirm Password must not be null.";
    public static final String CONFIRM_PASSWORD_NOT_BLANK = "Confirm Password must not be blank.";
    public static final String FULLNAME_NOT_BLANK = "Full name must not be blank.";
    public static final String FULLNAME_NOT_NULL = "Full name must not be null.";
    public static final String FULLNAME_INVALID = "Full name can only contain letters and spaces.";
    public static final String GENDER_NOT_NULL = "Gender must not be null.";
    public static final String AVATAR_INVALID_SIZE = "Avatar size must not exceed 2MB.";
    public static final String ROLE_NOT_NULL = "Role must not be null.";
}
