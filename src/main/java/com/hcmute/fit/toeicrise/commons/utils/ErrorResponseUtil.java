package com.hcmute.fit.toeicrise.commons.utils;

import com.hcmute.fit.toeicrise.commons.bases.ExceptionResponse;
import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;

public final class ErrorResponseUtil {
    private ErrorResponseUtil() {}

    public static ExceptionResponse buildResponseFromErrorCode(ErrorCode errorCode, String path, Object message) {
        return ExceptionResponse.builder()
                .timestamp(LocalDateTime.now(ZoneId.of(Constant.TIMEZONE_VIETNAM)))
                .path(path)
                .httpStatusCode(errorCode.getHttpStatusCode())
                .errorCode(errorCode.name())
                .message(message)
                .build();
    }

    public static ExceptionResponse buildResponseForSecurity(String path, HttpStatus httpStatus, ErrorCode errorCode) {
        return ExceptionResponse.builder()
                .timestamp(LocalDateTime.now(ZoneId.of(Constant.TIMEZONE_VIETNAM)))
                .path(path)
                .httpStatusCode(httpStatus.value())
                .errorCode(errorCode.name())
                .message(errorCode.getMessage())
                .build();
    }
}
