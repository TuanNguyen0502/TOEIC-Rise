package com.hcmute.fit.toeicrise.exceptions.handlers;

import com.hcmute.fit.toeicrise.commons.bases.ExceptionResponse;
import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGenericException(Exception exception, HttpServletRequest request) {
        log.error("Unhandled exception at [{}]", request.getRequestURI(), exception);
        return buildResponseEntity(ErrorCode.UNCATEGORIZED_EXCEPTION, request.getRequestURI(), ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ExceptionResponse> handleAppException(AppException exception, HttpServletRequest request) {
        log.warn("AppException at [{}]: {} ({})", request.getRequestURI(),
                exception.getMessage(), exception.getErrorCode().name());
        return buildResponseEntity(exception.getErrorCode(), request.getRequestURI(), exception.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied at [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildResponseEntity(ErrorCode.UNAUTHORIZED, request.getRequestURI(), ErrorCode.UNAUTHORIZED.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(Exception ex, HttpServletRequest request) {
        log.debug("Validation error at [{}]: {}", request.getRequestURI(), ex.getMessage());
        if (ex instanceof MethodArgumentNotValidException validationEx) {
            Map<String, String> messages = new HashMap<>();
            for (FieldError fieldError : validationEx.getBindingResult().getFieldErrors()) {
                messages.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
            }
            return buildResponseEntity(ErrorCode.VALIDATION_ERROR, request.getRequestURI(), messages);
        } else {
            return buildResponseEntity(ErrorCode.VALIDATION_ERROR, request.getRequestURI(), ErrorCode.VALIDATION_ERROR.getMessage());
        }
    }

    @ExceptionHandler({MaxUploadSizeExceededException.class, MultipartException.class})
    public ResponseEntity<ExceptionResponse> handleMaxUploadSizeExceeded(MultipartException ex, HttpServletRequest request) {
        log.warn("File upload size exceeded at [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildResponseEntity(ErrorCode.FILE_SIZE_EXCEEDED, request.getRequestURI(), ErrorCode.FILE_SIZE_EXCEEDED.getMessage());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ExceptionResponse> handleConstraintViolation(ConstraintViolationException exception, HttpServletRequest request) {
        log.debug("ConstraintViolation at [{}]: {}", request.getRequestURI(), exception.getMessage());
        Map<String, String> messages = new HashMap<>();
        for (ConstraintViolation<?> constraintViolation : exception.getConstraintViolations()) {
            messages.putIfAbsent(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
        }
        return buildResponseEntity(ErrorCode.VALIDATION_ERROR, request.getRequestURI(), messages);
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
                .httpStatusCode(errorCode.getHttpStatusCode())
                .errorCode(errorCode.name())
                .message(message)
                .build();
    }
}