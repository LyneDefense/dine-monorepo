package com.dine.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public static AccountUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AccountUserDetails) {
            return (AccountUserDetails) authentication.getPrincipal();
        }
        return null;
    }

    public static Long getCurrentAccountId() {
        AccountUserDetails user = getCurrentUser();
        return user != null ? user.getAccountId() : null;
    }

    public static Long getCurrentRestaurantId() {
        AccountUserDetails user = getCurrentUser();
        return user != null ? user.getRestaurantId() : null;
    }

    public static String getCurrentRole() {
        AccountUserDetails user = getCurrentUser();
        return user != null ? user.getRole() : null;
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof AccountUserDetails;
    }
}
