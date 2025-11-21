package com.cherry.protocol.enums;

public enum MsgType {
    TEXT(1),    // 文本
    IMAGE(2),   // 图片
    VIDEO(3),   // 视频
    VOICE(4);   // 语音

    public final int code;
    MsgType(int code) { this.code = code; }
}
