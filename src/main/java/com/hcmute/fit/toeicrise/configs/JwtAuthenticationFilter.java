package com.hcmute.fit.toeicrise.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.services.interfaces.IJwtService;
import com.hcmute.fit.toeicrise.services.interfaces.ITokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final IJwtService jwtService;
    private final ITokenBlacklistService tokenBlacklistService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);

            // Check if token is blacklisted
            if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
                // return 401 Unauthorized
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "Token has been invalidated");
                return; // Stop the filter chain here
            }

            Claims claims = jwtService.extractAllClaims(jwt);

            final String userEmail = claims.getSubject();

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (userEmail != null && authentication == null) {

                Boolean isActive = claims.get("isActive", Boolean.class);
                if (isActive != null && !isActive) {
                    sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN", "Account is deactivated");
                    return;
                }

                Object rolesObject = claims.get("roles");
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                if (rolesObject instanceof List<?>) {
                    authorities = ((List<?>) rolesObject).stream()
                            .map(Object::toString)
                            .map(SimpleGrantedAuthority::new)
                            .toList();
                }

                org.springframework.security.core.userdetails.User principal =
                        new org.springframework.security.core.userdetails.User(userEmail, "", authorities);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        authorities
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException exception) {
            // Handle expired JWT specifically
            ErrorCode errorCode = ErrorCode.TOKEN_EXPIRED;
            sendErrorResponse(response, errorCode.getHttpStatus().value(), errorCode.name(), errorCode.getMessage());
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String error, String message) throws IOException {
        SecurityContextHolder.clearContext();
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("status", status);

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
