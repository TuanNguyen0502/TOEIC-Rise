package com.hcmute.fit.toeicrise.services.interfaces;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface IJwtService {
    String extractUsername(String token);

    List<String> extractRoles(String token);

    Boolean extractIsActive(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    String generateTokenFromUser(com.hcmute.fit.toeicrise.models.entities.User user);

    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

    long getExpirationTime();

    String generateTokenResetPassword(UserDetails userDetails);

    boolean isPasswordResetTokenValid(String token);

    boolean isTokenExpired(String token);
}
