package com.hcmute.fit.toeicrise.commons.constants;

public class MessageConstant {
    public static final String INVALID_EMAIL = "Invalid email! Please enter a valid email address.";
    public static final String INVALID_PASSWORD = "Invalid password! Password must be 8-20 characters long, contain at least one digit, one lowercase letter, one uppercase letter, one special character from .@#$%^&+=, and have no whitespace.";
    public static final String INVALID_OTP = "Invalid OTP! OTP must be exactly 6 digits (0-9).";
    public static final String INVALID_TEST_SET = "Test set name can only contain letters, digits, spaces, and parentheses.";
    // Profile messages
    public static final String PROFILE_FULLNAME_NOT_NULL = "Full name must not be null.";
    public static final String PROFILE_FULLNAME_NOT_BLANK = "Full name must not be blank.";
    public static final String PROFILE_FULLNAME_INVALID = "Full name can only contain letters and spaces.";
    public static final String PROFILE_AVATAR_SIZE_EXCEEDED = "Avatar file size must not exceed 2MB.";
    public static final String PROFILE_GENDER_NOT_NULL = "Gender must not be null.";
    // Page messages
    public static final String INVALID_INPUT_DATA = "Invalid input data";
    public static final String PAGE_MIN = "Page index must be zero or greater.";
    public static final String SIZE_PAGE_MIN = "Size must be greater than ten.";
    public static final String PAGE_MAX = "Page index must be less than or equal to 100.";
    public static final String SIZE_PAGE_MAX = "Size must be less than or equal to 50.";
    // Question Group messages
    public static final String QUESTION_GROUP_TRANSCRIPT_NOT_BLANK = "Question group transcript must not be blank.";
    public static final String QUESTION_GROUP_TRANSCRIPT_NOT_NULL = "Question group transcript must not be null.";
    public static final String QUESTION_GROUP_AUDIO_SIZE_EXCEEDED = "Question group audio size must not exceed 10MB.";
    public static final String QUESTION_GROUP_IMAGE_SIZE_EXCEEDED = "Question group image size must not exceed 5MB.";
    public static final String QUESTION_GROUP_AUDIO_URL_INVALID = "Question group audio URL is invalid.";
    public static final String QUESTION_GROUP_IMAGE_URL_INVALID = "Question group image URL is invalid.";
    public static final String QUESTION_GROUP_AUDIO_URL_FORMAT_INVALID = "Question group audio URL format is invalid.";
    public static final String QUESTION_GROUP_IMAGE_URL_FORMAT_INVALID = "Question group image URL format is invalid.";
    // System Prompt related messages
    public static final String SYSTEM_PROMPT_CONTENT_NOT_BLANK = "Content must not be blank";
    public static final String SYSTEM_PROMPT_CONTENT_INVALID = "Content must be at least 20 characters and can only contain letters, digits, spaces, and punctuation (.,!?()'\"-)";
    public static final String SYSTEM_PROMPT_CONTENT_NOT_NULL = "Content must not be null";
    public static final String SYSTEM_PROMPT_IS_ACTIVE_NOT_NULL = "isActive must not be null";
    // Chatbot messages
    public static final String CHAT_MESSAGE_NOT_BLANK = "Message must not be blank";
    public static final String CHAT_TITLE_NOT_BLANK = "Title must not be blank";
    public static final String CHAT_CONVERSATION_ID_NOT_BLANK = "Conversation ID must not be blank";
    public static final String CHAT_TITLE_INVALID = "Title can only contain letters, digits, spaces, and punctuation (.,!?()'\"-), and must be between 1 and 100 characters long.";
    public static final String CHAT_CONVERSATION_ID_INVALID = "Conversation ID is invalid.";
    public static final String MESSAGE_ID_NOT_BLANK = "Message ID must not be blank";
    public static final String ECHATBOT_RATING_NOT_NULL = "Rating must not be null";
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
    public static final String IS_ACTIVE_NOT_NULL = "isActive must not be null.";
//    Question related messages
    public static final String QUESTION_ID_NOT_NULL = "Question must not be null.";
    public static final String QUESTION_GROUP_ID_NOT_NULL = "Question group must not be null.";
    public static final String CORRECT_OPTION_NOT_NULL = "Correct option must not be null.";
    public static final String CORRECT_OPTION_NOT_BLANK = "Correct option must be blank.";
    public static final String EXPLAIN_NOT_BLANK = "Explain must not be blank.";
    public static final String EXPLAIN_NOT_NULL = "Explain must not be null.";
    public static final String TAG_NOT_NULL = "Tag must not be null.";
    public static final String TAG_NOT_EMPTY = "Tag must not be empty.";
}
