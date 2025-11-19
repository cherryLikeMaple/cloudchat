package com.cherry.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author cherry
 */
public class TokenUtils {
    /** 从 Authorization: Bearer xxx 里取 token */
    public static String resolveToken(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null) return null;
        if (auth.startsWith("Bearer ")) return auth.substring(7);
        return auth.trim();
    }
}
