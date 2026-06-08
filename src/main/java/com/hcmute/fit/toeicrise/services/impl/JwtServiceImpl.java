package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.services.interfaces.IJwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements IJwtService {
    @Value("${security.jwt.secret-key}")
    private String jwtSecretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpirationTime;

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        Object rolesObject = claims.get("roles");

        if (rolesObject instanceof List<?>) {
            return ((List<?>) rolesObject).stream()
                    .map(Object::toString)
                    .toList();
        }
        return java.util.Collections.emptyList();
    }

    @Override
    public Boolean extractIsActive(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("isActive", Boolean.class);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public String generateTokenFromUser(com.hcmute.fit.toeicrise.models.entities.User user) {
        if (user.getAccount() == null) {
            throw new com.hcmute.fit.toeicrise.exceptions.AppException(
                    com.hcmute.fit.toeicrise.models.enums.ErrorCode.INVALID_CREDENTIALS
            );
        }

        Map<String, Object> extraClaims = new HashMap<>();

        String roleName = "ROLE_" + user.getRole().getName().name();
        extraClaims.put("roles", java.util.List.of(roleName));

        extraClaims.put("isActive", user.getAccount().getIsActive());
        String email = user.getAccount().getEmail();

        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(
                        email,
                        "",
                        java.util.Collections.emptyList()
                );

        return buildToken(extraClaims, userDetails, jwtExpirationTime);
    }

    @Override
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpirationTime);
    }

    @Override
    public long getExpirationTime() {
        return jwtExpirationTime;
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateTokenResetPassword(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("resetPwd", true);
        return generateToken(claims, userDetails);
    }

    @Override
    public boolean isPasswordResetTokenValid(String token) {
        Claims claims = extractAllClaims(token);
        return Boolean.TRUE.equals(claims.get("resetPwd"));
    }

    @Override
    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return true;
        }
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
