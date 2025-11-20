package com.cherry.dto.ws;

public enum ClientType {
    WEB("web", "网页端"),
    IOS("ios", "苹果客户端"),
    ANDROID("android", "安卓客户端"),
    PC("pc", "桌面端"),
    MINI_PROGRAM("mini_program", "小程序");

    private final String code;
    private final String desc;

    ClientType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据code获取枚举
     */
    public static ClientType getByCode(String code) {
        for (ClientType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
