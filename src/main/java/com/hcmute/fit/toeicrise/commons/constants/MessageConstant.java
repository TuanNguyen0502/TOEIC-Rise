package com.hcmute.fit.toeicrise.commons.constants;

public class MessageConstant {
    public static final String INVALID_EMAIL = "Invalid email! Please enter a valid email address.";
    public static final String INVALID_PASSWORD = "Invalid password! Password must be 8-20 characters long, contain at least one digit, one lowercase letter, one uppercase letter, one special character from .@#$%^&+=, and have no whitespace.";
    public static final String INVALID_OTP = "Invalid OTP! OTP must be exactly 6 digits (0-9).";
    public static final String INVALID_TEST_SET = "Test set name can only contain letters, digits, spaces, and parentheses.";
    // Profile messages
    public static final String INVALID_USER = "Invalid or anonymous user";
    public static final String PROFILE_FULLNAME_NOT_NULL = "Full name must not be null.";
    public static final String PROFILE_FULLNAME_NOT_BLANK = "Full name must not be blank.";
    public static final String PROFILE_FULLNAME_INVALID = "Full name can only contain letters and spaces.";
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
    public static final String QUESTION_GROUP_AUDIO_URL_INVALID = "Question group audio URL is invalid.";
    public static final String QUESTION_GROUP_IMAGE_URL_INVALID = "Question group image URL is invalid.";
    public static final String QUESTION_GROUP_AUDIO_URL_FORMAT_INVALID = "Question group audio URL format is invalid.";
    public static final String QUESTION_GROUP_IMAGE_URL_FORMAT_INVALID = "Question group image URL format is invalid.";
    public static final String QUESTION_GROUP_PASSAGE_NOT_BLANK = "Question group passage must not be blank.";
    // System Prompt related messages
    public static final String SYSTEM_PROMPT_CONTENT_NOT_BLANK = "Content must not be blank";
    public static final String SYSTEM_PROMPT_CONTENT_INVALID = "Content must be at least 20 characters and can only contain letters, digits, spaces, and allowed punctuation (!#%&*()[]_+;:'\",.<>?/{}-\\n\\r–).";
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
    public static final String ROLE_NOT_NULL = "Role must not be null.";
    public static final String IS_ACTIVE_NOT_NULL = "isActive must not be null.";
    // Question related messages
    public static final String QUESTION_ID_NOT_NULL = "Question must not be null.";
    public static final String QUESTION_GROUP_ID_NOT_NULL = "Question group must not be null.";
    public static final String QUESTION_GROUP_ID_NOT_EMPTY = "Question group must not be empty";
    public static final String QUESTION_CORRECT_OPTION_NOT_NULL = "Correct option must not be null.";
    public static final String QUESTION_CORRECT_OPTION_NOT_BLANK = "Correct option must not be blank.";
    public static final String QUESTION_EXPLANATION_NOT_BLANK = "Explain must not be blank.";
    public static final String QUESTION_EXPLANATION_NOT_NULL = "Explain must not be null.";
    public static final String QUESTION_MIN = "Question size must be greater than 5.";
    public static final String QUESTION_MAX = "Question size must be less than 60.";
    public static final String USER_ANSWER_NOT_NULL = "User answer must not be null.";
    public static final String USER_ANSWER_NOT_EMPTY = "User answer must not be empty.";
    // User Test related messages
    public static final String TEST_ID_NOT_NULL = "Test ID must not be null.";
    public static final String TIME_SPENT_MIN = "Time spent must be at least 1 second.";
    public static final String ANSWERS_NOT_EMPTY = "Answers must not be empty.";
    // Question Report related messages
    public static final String QUESTION_REPORT_REASONS_NOT_EMPTY = "Report reasons must not be empty.";
    public static final String QUESTION_REPORT_RESOLVED_NOTE_NOT_NULL = "Resolved note must not be null.";
    public static final String QUESTION_REPORT_STATUS_NOT_NULL = "Report status must not be null.";
    // Flashcard related messages
    public static final String FLASHCARD_NAME_NOT_NULL = "Flashcard name must not be null.";
    public static final String FLASHCARD_NAME_NOT_BLANK = "Flashcard name must not be blank.";
    public static final String FLASHCARD_NAME_INVALID = "Flashcard name can only contain letters, digits, spaces, and special characters ().,'- and must be between 1 and 100 characters long.";
    public static final String FLASHCARD_ACCESS_TYPE_NOT_NULL = "Flashcard access type must not be null.";
    public static final String FLASHCARD_ID_NOT_NULL = "Flashcard ID must not be null.";
    // Flashcard Item related messages
    public static final String FLASHCARD_ITEM_VOCABULARY_NOT_BLANK = "Flashcard item vocabulary must not be blank.";
    public static final String FLASHCARD_ITEM_DEFINITION_NOT_BLANK = "Flashcard item definition must not be blank.";
    public static final String FLASHCARD_ITEM_ID_NOT_NULL = "Flashcard item id must not be null.";
    // Part related messages
    public static final String PART_ID_MIN = "Part ID must be greater than 1.";
    public static final String PART_ID_MAX = "Part ID must be less than 7.";
    public static final String PART_NAME_NOT_BLANK = "Part name must not be blank.";
    // Tag related messages
    public static final String TAGS_SIZE = "Tags size must be between 1 and 3.";
    public static final String TAG_NOT_NULL = "Tag must not be null.";
    public static final String TAG_NOT_EMPTY = "Tag must not be empty.";
    public static final String TAG_NAME_NOT_NULL = "Tag name must not be null.";
    public static final String TAG_NAME_NOT_BLANK = "Tag name must not be blank.";
    public static final String TAG_NAME_INVALID = "Tag name can only contain letters, digits, spaces, and special characters []():.,'- and must be between 1 and 50 characters long.";
    // FlashcardItemProgress related messages
    public static final String FLASHCARD_ITEM_PROGRESS_NOT_EMPTY = "Flashcard item progress must not be empty.";
    public static final String SENTENCE_NOT_EMPTY = "Sentence must not be empty";
    public static final String SENTENCE_NOT_BLANK = "Sentence must not be blank";
    public static final String KEYWORD_NOT_EMPTY = "Keyword must not be empty.";
    public static final String KEYWORD_NOT_BLANK = "Keyword must not be blank.";
    // LearningPath related messages
    public static final String LEARNING_PATH_NAME_NOT_BLANK = "Learning path name must not be blank.";
    public static final String LEARNING_PATH_NAME_NOT_NULL = "Learning path name must not be null.";
    public static final String LEARNING_PATH_NAME_MAX = "Learning path name must not exceed 255 characters.";
    public static final String LEARNING_PATH_DESCRIPTION_MAX = "Description must not exceed 5000 characters.";
    // Lesson related messages
    public static final String EITHER_MULTIPLE_OR_URL_REQUIRED = "Either video file or videoUrl must be provided";
    public static final String LESSONS_NOT_EMPTY = "Lessons must not be empty.";
    public static final String LESSON_ID_NOT_NULL = "LessonId must not be null.";
    public static final String LESSON_ORDER_INDEX_NOT_NULL = "Order index must not be null.";
    public static final String LESSON_ORDER_INDEX_MIN = "Order index must be greater than 0.";
    public static final String LESSON_TITLE_NOT_NULL = "Title must not be null.";
    public static final String LESSON_TITLE_NOT_BLANK = "Title must not be blank.";
    public static final String LESSON_TITLE_MAX = "Title must not exceed 255 characters.";
    public static final String LESSON_VIDEO_URL_MAX = "Video URL must not exceed 500 characters.";
    public static final String LESSON_TOPIC_MAX = "Topic must not exceed 255 characters.";
    public static final String LESSON_TOPIC_NOT_BLANK = "Topic must not be blank.";
    public static final String LESSON_TOPIC_NOT_NULL = "Topic must not be null";
    public static final String LESSON_LEVEL_NOT_NULL = "Level must not be null.";
    public static final String LESSON_DO_NOT_BELONG_LEARNING_PATH = "Lesson does not belong to this learning path";
    // User progress related messages
    public static final String USER_PROGRESS_PERCENT_NOT_NULL = "ProgressPercentage must not be null.";
    public static final String USER_PROGRESS_PERCENT_MIN = "ProgressPercentage must be >= 0.";
    public static final String USER_PROGRESS_PERCENT_MAX = "ProgressPercentage must be <= 100.";
    public static final String USER_PROGRESS_LAST_WATCHED_TIME_MS_NOT_NULL = "LastWatchedTimeMs must not be null.";
    public static final String USER_PROGRESS_LAST_WATCHED_TIME_MS_MIN = "LastWatchedTimeMs must be >= 0.";
    // Success messages
    public static final String REGISTRATION_SUCCESS = "Registration successful. Please check your email for the verification code.";
    public static final String ACCOUNT_VERIFICATION_SUCCESS = "Account verified successfully.";
    public static final String VERIFICATION_CODE_SUCCESS = "Verification code sent.";
    public static final String PASSWORD_RESET_SUCCESS = "Password reset successfully.";
    public static final String PASSWORD_UPDATE_SUCCESS = "Password updated successfully.";
    public static final String PROFILE_UPDATE_SUCCESS = "Profile updated successfully.";
    public static final String TEST_SET_DELETED_SUCCESS = "Test Set deleted successfully.";
    public static final String TEST_SET_CREATED_SUCCESS = "Test Set created successfully.";
    public static final String TEST_IMPORT_SUCCESS = "Test imported successfully";
    public static final String QUESTION_UPDATE_SUCCESS = "Update question updated successfully";
    public static final String LEARNING_PATH_CREATED_SUCCESS = "Learning path created successfully.";
    public static final String LEARNING_PATH_UPDATED_SUCCESS = "Learning path updated successfully.";
    public static final String LESSON_REORDER_SUCCESS = "Lesson reorder successfully.";
    public static final String LESSON_ACTIVE_UPDATE_SUCCESS = "Lesson update active successfully.";
    //Common messages
    public static final String COMMENT_NOT_BLANK = "Comment content must not be blank.";
    public static final String COMMENT_TOO_LONG = "Comment content must not exceed 500 characters.";
    // Blog category
    public static final String BLOG_CATEGORY_NAME_NOT_BLANK = "Blog category name must not be blank.";
    public static final String BLOG_CATEGORY_NAME_INVALID = "Blog category name can only contain letters, digits, spaces, and punctuation (.,&()-), and must be between 2 and 50 characters long.";
    public static final String BLOG_CATEGORY_SLUG_NOT_BLANK = "Blog category slug must not be blank.";
    public static final String BLOG_CATEGORY_SLUG_INVALID = "Blog category slug can only contain lowercase letters, digits, and hyphens, and must be between 1 and 50 characters long.";
    public static final String BLOG_CATEGORY_ACTIVE_NOT_NULL = "Blog category active must not be null.";
    // Blog post
    public static final String BLOG_POST_TITLE_NOT_BLANK = "Blog post title must not be blank.";
    public static final String BLOG_POST_TITLE_INVALID = "Blog post title can only contain letters, digits, spaces, and punctuation (. , ! ? : ' \" ( ) -), and must be between 5 and 150 characters long.";
    public static final String BLOG_POST_SLUG_NOT_BLANK = "Blog post slug must not be blank.";
    public static final String BLOG_POST_SLUG_INVALID = "Blog post slug can only contain lowercase letters, digits, and hyphens, and must be between 1 and 50 characters long.";
    public static final String BLOG_POST_SUMMARY_NOT_BLANK = "Blog post summary must not be blank.";
    public static final String BLOG_POST_SUMMARY_INVALID = "Blog post summary can only contain letters, digits, spaces, and punctuation (. , ! ? : ' \" ( ) -), and must be between 10 and 500 characters long.";
    public static final String BLOG_POST_CONTENT_NOT_BLANK = "Blog post content must not be blank.";
    public static final String BLOG_POST_CONTENT_INVALID = "Blog post content must be at least 50 characters long.";
    public static final String BLOG_POST_THUMBNAIL_NOT_NULL = "Blog post thumbnail must not be null.";
    public static final String BLOG_POST_STATUS_NOT_NULL = "Blog post status must not be null.";
    public static final String BLOG_POST_IMAGE_NOT_BLANK = "Blog post image must not be blank.";
}
