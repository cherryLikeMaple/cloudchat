package com.cherry.dto.ws;

public enum ChatType {
    SINGLE(1),  // 单聊
    GROUP(2);   // 群聊

    public final int code;
    ChatType(int code) { this.code = code; }
}
