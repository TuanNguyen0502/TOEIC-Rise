package com.hcmute.fit.toeicrise.exceptions.handlers;

import com.hcmute.fit.toeicrise.commons.bases.ExceptionResponse;
import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        return buildResponseEntity(ErrorCode.UNCATEGORIZED_EXCEPTION,
                request.getRequestURI(),
                ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ExceptionResponse> handleAppException(AppException ex, HttpServletRequest request) {
        return buildResponseEntity(ex.getErrorCode(),
                request.getRequestURI(), ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        return buildResponseEntity(ErrorCode.UNAUTHORIZED,
                request.getRequestURI(),
                ErrorCode.UNAUTHORIZED.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(Exception ex, HttpServletRequest request) {
        if (ex instanceof MethodArgumentNotValidException validException) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError fieldError : validException.getBindingResult().getFieldErrors()) {
                errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
            }
            return buildResponseEntity(ErrorCode.VALIDATION_ERROR, request.getRequestURI(), errors);
        }
        return buildResponseEntity(ErrorCode.VALIDATION_ERROR, request.getRequestURI(), ErrorCode.VALIDATION_ERROR.getMessage());
    }

    private ResponseEntity<ExceptionResponse> buildResponseEntity(ErrorCode errorCode, String path, Object message) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(buildErrorResponse(errorCode, path, message));
    }

    private ExceptionResponse buildErrorResponse(ErrorCode errorCode, String path, Object message) {
        return ExceptionResponse.builder()
                .timestamp(LocalDateTime.now(
                        ZoneId.of(Constant.TIMEZONE_VIETNAM)
                )).path(path)
                .code(errorCode.getHttpStatusCode())
                .status(errorCode.name())
                .message(message)
                .build();
    }
}