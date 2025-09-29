package com.hcmute.fit.toeicrise.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.fit.toeicrise.dtos.responses.LoginResponse;
import com.hcmute.fit.toeicrise.services.interfaces.IAuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final ObjectProvider<IAuthenticationService> authenticationServiceProvider;
    private final ObjectMapper objectMapper;

    @Value("${frontend.callback-url}")
    private String frontendCallbackUrl;
    public CustomOAuth2SuccessHandler(@Lazy ObjectProvider<IAuthenticationService> authenticationServiceProvider,
                                      ObjectMapper objectMapper) {
        this.authenticationServiceProvider = authenticationServiceProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            String picture = oAuth2User.getAttribute("picture");

            // Get the service instance only when needed
            IAuthenticationService authenticationService = authenticationServiceProvider.getObject();

            // Call service to handle Google login
            LoginResponse loginResponse = authenticationService.loginWithGoogle(email, name, picture);

            // Create refresh token
            String refreshToken = authenticationService.createRefreshToken(email);
            long refreshTokenExpirationTime = authenticationService.getRefreshTokenDurationMs();

            // Create an HttpOnly cookie with the refresh token
            ResponseCookie responseCookie = ResponseCookie.from("refresh_token", refreshToken)
                    .httpOnly(true) // Cannot be accessed by JavaScript → enhances security
                    .secure(false) // Set to false for localhost development
                    .path("/") // Cookie is valid for the entire system
                    .maxAge(refreshTokenExpirationTime) // Cookie lifetime
                    .build();

            // Add the cookie to the response
            response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

            // Prepare user data for frontend
            String userData = objectMapper.writeValueAsString(loginResponse);

            // Redirect to frontend with token and user data
            String redirectUrl = frontendCallbackUrl +
                    "?google_success=true" +
                    "&access_token=" + URLEncoder.encode(loginResponse.getAccessToken(), StandardCharsets.UTF_8) +
                    "&user_data=" + URLEncoder.encode(userData, StandardCharsets.UTF_8);

            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            String redirectUrl = frontendCallbackUrl +
                    "?google_error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);

            response.sendRedirect(redirectUrl);
        }
    }
}
