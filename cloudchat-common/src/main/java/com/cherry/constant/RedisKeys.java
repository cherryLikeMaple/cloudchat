package com.cherry.constant;

public interface RedisKeys {
    String LOGIN_TOKEN = "LOGIN:TOKEN:";     // LOGIN:TOKEN:{token} -> userId
    String LOGIN_UID   = "LOGIN:UID:";       // LOGIN:UID:{userId} -> Set<token>
    String USER_PROFILE = "USER:PROFILE:";   // USER:PROFILE:{userId} -> json
}

