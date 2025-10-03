package com.hcmute.fit.toeicrise.models.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // === 1. Authentication & Authorization ===
    UNAUTHENTICATED("Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED("You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS("Email or password is incorrect", HttpStatus.UNAUTHORIZED),
    ACCOUNT_LOCKED("Your account is locked", HttpStatus.UNAUTHORIZED),
    UNVERIFIED_ACCOUNT("Account is not verified", HttpStatus.UNAUTHORIZED),
    VERIFIED_ACCOUNT("Account is already verified", HttpStatus.BAD_REQUEST),
    OTP_NOT_VERIFIED("OTP has not been verified", HttpStatus.UNAUTHORIZED),
    DUPLICATE_EMAIL("Email already exists", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("Token has expired", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("Token is invalid", HttpStatus.UNAUTHORIZED),

    // === 2. Validation Errors ===
    VALIDATION_ERROR("Invalid input value.", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH("Passwords do not match", HttpStatus.BAD_REQUEST),
    INVALID_OTP("%s OTP is invalid", HttpStatus.BAD_REQUEST),
    OTP_EXPIRED("OTP has expired", HttpStatus.BAD_REQUEST),

    // === 3. Business Logic Errors ===
    OTP_LIMIT_EXCEEDED("Exceeded %s OTP limit. Please try again after 30 minutes.", HttpStatus.TOO_MANY_REQUESTS),
    REGISTRATION_EXPIRED("Registration session has expired. Please register again.", HttpStatus.GONE),
    SYSTEM_PROMPT_CANNOT_DEACTIVATE("Cannot deactivate the only active system prompt.", HttpStatus.BAD_REQUEST),

    // === 4. Resource / Data Not Found or Conflicts ===
    RESOURCE_NOT_FOUND("%s not found", HttpStatus.NOT_FOUND),
    RESOURCE_ALREADY_EXISTS("%s already exists", HttpStatus.CONFLICT),

    // === 5. External Services / Upload Errors ===
    UPLOAD_FAILED("Failed to upload file", HttpStatus.NOT_IMPLEMENTED),
    IMAGE_DELETE_FAILED("Failed to delete image", HttpStatus.NOT_IMPLEMENTED),
    INVALID_FILE_FORMAT("The file format you sent is invalid", HttpStatus.NOT_IMPLEMENTED),
    MAIL_SEND_FAILED("Failed to send email", HttpStatus.SERVICE_UNAVAILABLE),
    IMAGE_SIZE_EXCEEDED("Image size exceeds the limit", HttpStatus.NOT_IMPLEMENTED),
    FILE_READ_ERROR("Can not read file", HttpStatus.NOT_IMPLEMENTED),
    OTP_SEND_FAILED("Failed to send OTP", HttpStatus.SERVICE_UNAVAILABLE),
    JSON_CONVERT_ERROR("Error converting list to JSON", HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_PROCESSING_ERROR("Error processing image", HttpStatus.INTERNAL_SERVER_ERROR),

    // === 9. System & Uncategorized ===
    DATABASE_ERROR("Database error", HttpStatus.NOT_IMPLEMENTED),
    GENERATE_TOKEN_EXCEPTION("Failed to generate token", HttpStatus.NOT_IMPLEMENTED),
    UNCATEGORIZED_EXCEPTION("Unexpected error occurred", HttpStatus.NOT_IMPLEMENTED),
    CACHE_ERROR("Cache error", HttpStatus.NOT_IMPLEMENTED);

    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public int getHttpStatusCode() {return httpStatus.value();}
}