package com.cherry.constant;

public class RedisKeys {
    public static final String LOGIN_TOKEN = "login:token:";          // token -> uid（保留）

    public static final String LOGIN_USER_SESSIONS = "login:user:sessions:"; // + uid -> Set<token>
    public static final String LOGIN_SESSION = "login:session:";            // + token -> Hash(uid, clientType, deviceId)
    
    public static final String UNREAD = "chat:unread:";
}


