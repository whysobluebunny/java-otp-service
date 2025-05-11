package ru.mephi.abondarenko.otpapp.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {
    public static String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
