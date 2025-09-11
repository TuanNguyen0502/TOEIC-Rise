package com.hcmute.fit.toeicrise.exceptions.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.fit.toeicrise.commons.bases.ExceptionResponse;
import com.hcmute.fit.toeicrise.commons.constants.Constant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
public class SecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
    private final ObjectMapper objectMapper;


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

    }

    private void handleSecurityException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         HttpStatus httpStatus,
                                         String message) throws IOException {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .timestamp(LocalDateTime.now(
                        ZoneId.of(Constant.TIMEZONE_VIETNAM)))
                .path(request.getRequestURI())
                .code(httpStatus.value())
                .status(httpStatus.name())
                .message(message)
                .build();
        response.setStatus(httpStatus.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.
                writeValueAsString(exceptionResponse));
    }
}