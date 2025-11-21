package com.hcmute.fit.toeicrise.exceptions.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.fit.toeicrise.commons.bases.ExceptionResponse;
import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.warn("Unauthenticated access to [{}]: {}", request.getRequestURI(), authException.getMessage(), authException);
        handleSecurityException(request, response, HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHENTICATED);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        log.warn("Access denied on [{}]: {}", request.getRequestURI(), accessDeniedException.getMessage(), accessDeniedException);
        handleSecurityException(request, response, HttpStatus.FORBIDDEN, ErrorCode.UNAUTHORIZED);
    }

    private void handleSecurityException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         HttpStatus httpStatus,
                                         ErrorCode errorCode) throws IOException {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .timestamp(LocalDateTime.now(
                        ZoneId.of(Constant.TIMEZONE_VIETNAM)))
                .path(request.getRequestURI())
                .httpStatusCode(httpStatus.value())
                .errorCode(errorCode.name())
                .message(errorCode.getMessage())
                .build();
        response.setStatus(httpStatus.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.
                writeValueAsString(exceptionResponse));
    }
}