package com.example.jungleroyal.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {
    private CustomUserDetails getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails;
        }
        return null;
    }
    public String getUserId() {
        CustomUserDetails userDetails = getAuthenticatedUser();
        return (userDetails != null) ? String.valueOf(userDetails.getUserId()) : "anonymous";
    }

    public String getUsername() {
        CustomUserDetails userDetails = getAuthenticatedUser();
        return (userDetails != null) ? userDetails.getUsername() : "anonymous";
    }

    public String getUserRole() {
        CustomUserDetails userDetails = getAuthenticatedUser();
        return (userDetails != null) ? userDetails.getUserRole().name() : "anonymous";
    }
}
