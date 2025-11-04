package com.hcmute.fit.toeicrise.commons.utils;

import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()||
                authentication instanceof AnonymousAuthenticationToken) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        String email = authentication.getName();
        if (email == null || email.isEmpty()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return email;
    }
}