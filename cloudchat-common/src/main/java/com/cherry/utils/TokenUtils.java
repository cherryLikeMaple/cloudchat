package com.cherry.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author cherry
 */
public class TokenUtils {
    public static String resolveToken(HttpServletRequest request) {
        return resolveFromHeader(request.getHeader("Authorization"));
    }

    /** 直接从字符串 header 里解析，方便 Feign / Controller 使用 */
    public static String resolveFromHeader(String auth) {
        if (auth == null || auth.isEmpty()) return null;
        if (auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        return auth.trim();
    }
}
