package com.hcmute.fit.toeicrise.commons.constants;

import java.time.Duration;
import java.util.Map;

public class Constant {
    // Estimated score maps
    public static final Map<Integer, Integer> estimatedReadingScoreMap = Map.<Integer, Integer>ofEntries(
            Map.entry(0, 5),
            Map.entry(1, 5),
            Map.entry(2, 5),
            Map.entry(3, 10),
            Map.entry(4, 15),
            Map.entry(5, 20),
            Map.entry(6, 25),
            Map.entry(7, 30),
            Map.entry(8, 35),
            Map.entry(9, 40),
            Map.entry(10, 45),
            Map.entry(11, 50),
            Map.entry(12, 55),
            Map.entry(13, 60),
            Map.entry(14, 65),
            Map.entry(15, 70),
            Map.entry(16, 75),
            Map.entry(17, 80),
            Map.entry(18, 85),
            Map.entry(19, 90),
            Map.entry(20, 95),
            Map.entry(21, 100),
            Map.entry(22, 105),
            Map.entry(23, 110),
            Map.entry(24, 115),
            Map.entry(25, 120),
            Map.entry(26, 125),
            Map.entry(27, 130),
            Map.entry(28, 135),
            Map.entry(29, 140),
            Map.entry(30, 145),
            Map.entry(31, 150),
            Map.entry(32, 155),
            Map.entry(33, 160),
            Map.entry(34, 165),
            Map.entry(35, 170),
            Map.entry(36, 175),
            Map.entry(37, 180),
            Map.entry(38, 185),
            Map.entry(39, 190),
            Map.entry(40, 195),
            Map.entry(41, 200),
            Map.entry(42, 205),
            Map.entry(43, 210),
            Map.entry(44, 215),
            Map.entry(45, 220),
            Map.entry(46, 225),
            Map.entry(47, 230),
            Map.entry(48, 235),
            Map.entry(49, 240),
            Map.entry(50, 245),
            Map.entry(51, 250),
            Map.entry(52, 255),
            Map.entry(53, 260),
            Map.entry(54, 265),
            Map.entry(55, 270),
            Map.entry(56, 275),
            Map.entry(57, 280),
            Map.entry(58, 285),
            Map.entry(59, 290),
            Map.entry(60, 295),
            Map.entry(61, 300),
            Map.entry(62, 305),
            Map.entry(63, 310),
            Map.entry(64, 315),
            Map.entry(65, 320),
            Map.entry(66, 325),
            Map.entry(67, 330),
            Map.entry(68, 335),
            Map.entry(69, 340),
            Map.entry(70, 345),
            Map.entry(71, 350),
            Map.entry(72, 355),
            Map.entry(73, 360),
            Map.entry(74, 365),
            Map.entry(75, 370),
            Map.entry(76, 375),
            Map.entry(77, 380),
            Map.entry(78, 385),
            Map.entry(79, 390),
            Map.entry(80, 395),
            Map.entry(81, 400),
            Map.entry(82, 405),
            Map.entry(83, 410),
            Map.entry(84, 415),
            Map.entry(85, 420),
            Map.entry(86, 425),
            Map.entry(87, 430),
            Map.entry(88, 435),
            Map.entry(89, 440),
            Map.entry(90, 445),
            Map.entry(91, 450),
            Map.entry(92, 455),
            Map.entry(93, 460),
            Map.entry(94, 465),
            Map.entry(95, 470),
            Map.entry(96, 475),
            Map.entry(97, 480),
            Map.entry(98, 485),
            Map.entry(99, 490),
            Map.entry(100, 495)
    );
    public static final Map<Integer, Integer> estimatedListeningScoreMap = Map.<Integer, Integer>ofEntries(
            Map.entry(0, 5),
            Map.entry(1, 15),
            Map.entry(2, 20),
            Map.entry(3, 25),
            Map.entry(4, 30),
            Map.entry(5, 35),
            Map.entry(6, 40),
            Map.entry(7, 45),
            Map.entry(8, 50),
            Map.entry(9, 55),
            Map.entry(10, 60),
            Map.entry(11, 65),
            Map.entry(12, 70),
            Map.entry(13, 75),
            Map.entry(14, 80),
            Map.entry(15, 85),
            Map.entry(16, 90),
            Map.entry(17, 95),
            Map.entry(18, 100),
            Map.entry(19, 105),
            Map.entry(20, 110),
            Map.entry(21, 115),
            Map.entry(22, 120),
            Map.entry(23, 125),
            Map.entry(24, 130),
            Map.entry(25, 135),
            Map.entry(26, 140),
            Map.entry(27, 145),
            Map.entry(28, 150),
            Map.entry(29, 155),
            Map.entry(30, 160),
            Map.entry(31, 165),
            Map.entry(32, 170),
            Map.entry(33, 175),
            Map.entry(34, 180),
            Map.entry(35, 185),
            Map.entry(36, 190),
            Map.entry(37, 195),
            Map.entry(38, 200),
            Map.entry(39, 205),
            Map.entry(40, 210),
            Map.entry(41, 215),
            Map.entry(42, 220),
            Map.entry(43, 225),
            Map.entry(44, 230),
            Map.entry(45, 235),
            Map.entry(46, 240),
            Map.entry(47, 245),
            Map.entry(48, 250),
            Map.entry(49, 255),
            Map.entry(50, 260),
            Map.entry(51, 265),
            Map.entry(52, 270),
            Map.entry(53, 275),
            Map.entry(54, 280),
            Map.entry(55, 285),
            Map.entry(56, 290),
            Map.entry(57, 295),
            Map.entry(58, 300),
            Map.entry(59, 305),
            Map.entry(60, 310),
            Map.entry(61, 315),
            Map.entry(62, 320),
            Map.entry(63, 325),
            Map.entry(64, 330),
            Map.entry(65, 335),
            Map.entry(66, 340),
            Map.entry(67, 345),
            Map.entry(68, 350),
            Map.entry(69, 355),
            Map.entry(70, 360),
            Map.entry(71, 365),
            Map.entry(72, 370),
            Map.entry(73, 375),
            Map.entry(74, 380),
            Map.entry(75, 385),
            Map.entry(76, 395),
            Map.entry(77, 400),
            Map.entry(78, 405),
            Map.entry(79, 410),
            Map.entry(80, 415),
            Map.entry(81, 420),
            Map.entry(82, 425),
            Map.entry(83, 430),
            Map.entry(84, 435),
            Map.entry(85, 440),
            Map.entry(86, 445),
            Map.entry(87, 450),
            Map.entry(88, 455),
            Map.entry(89, 460),
            Map.entry(90, 465),
            Map.entry(91, 470),
            Map.entry(92, 475),
            Map.entry(93, 480),
            Map.entry(94, 485),
            Map.entry(95, 490),
            Map.entry(96, 495),
            Map.entry(97, 495),
            Map.entry(98, 495),
            Map.entry(99, 495),
            Map.entry(100, 495)
    );
    // Regex patterns
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String TIMEZONE_VIETNAM = "Asia/Ho_Chi_Minh";
    // User authentication patterns
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[.@#$%^&+=])(?=\\S+$).{8,20}$";
    public static final String OTP_PATTERN = "^\\d{6}$";
    // Test and test set name patterns
    public static final String TEST_SET_NAME_PATTERN = "^[\\p{L}0-9 ()]+$";
    public static final String TEST_NAME_PATTERN = "^[\\p{L}0-9 ()]+$";
    // Chatbot patterns
    public static final String SYSTEM_PROMPT_CONTENT_PATTERN = "^[\\p{L}0-9 .,!?()'\"-]{20,}$";
    public static final String CHAT_TITLE_PATTERN = "^[\\p{L}0-9 .,!?()'\"-]{1,100}$";
    public static final String CHAT_CONVERSATION_ID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    // Cache constants
    public static final String SYSTEM_PROMPT_CACHE = "systemPrompt";
    public static final String ACTIVE_PROMPT_KEY = "active";
    public static final Duration CACHE_DURATION = Duration.ofDays(30);
    // Profile constants
    public static final String PROFILE_FULLNAME_PATTERN = "^[\\p{L} ]+$";
    public static final int PROFILE_AVATAR_MAX_SIZE = 2 * 1024 * 1024; // 2MB
    // Question group
    public static final int QUESTION_GROUP_AUDIO_MAX_SIZE = 10 * 1024 * 1024; // 10MB
    public static final int QUESTION_GROUP_IMAGE_MAX_SIZE = 5 * 1024 * 1024;  // 5MB
    public static final String QUESTION_GROUP_AUDIO_URL_FORMAT = "^(https?://.*\\.(mp3|wav|ogg|m4a|aac))$";
    public static final String QUESTION_GROUP_IMAGE_URL_FORMAT = "^(https?://.*\\.(jpg|jpeg|png|gif|bmp))$";
    // User constants
    public static final String FULLNAME_PATTERN = "^[\\p{L} ]+$";
    public static final int AVATAR_MAX_SIZE = 2 * 1024 * 1024; // 2MB
    // Flashcard constants
    public static final String FLASHCARD_NAME_PATTERN = "^[\\p{L}0-9 ().,'-]{1,100}$";
    // Authentication constants
    public static final int LOCK_DURATION_MINUTES = 30;
    public static final int MAX_VERIFY_OTP_TIMES = 5;
    public static final int MAX_RESEND_OTP_ATTEMPTS = 5;
    public static final int OTP_EXPIRATION_MINUTES = 5;
    public static final int MAX_VERIFY_LOGIN_TIMES = 15;
}