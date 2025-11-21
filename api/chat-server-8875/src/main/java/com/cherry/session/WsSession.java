package com.cherry.session;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class WsSession {

    /**
     * 真实用户 id
     */
    private Long userId;

    /**
     * 客户端类型：web / ios / android / windows / mac ...
     */
    private String clientType;

    /**
     * 设备 id：前端生成并持久化（localStorage 或 手机设备号）
     */
    private String deviceId;

    /**
     * 登录 token（便于排查问题）
     */
    private String token;

    /**
     * 对应的 Netty Channel
     */
    private Channel channel;

    /**
     * 唯一标识一个“端”
     */
    public String uniqueKey() {
        return userId + ":" + clientType + ":" + deviceId;
    }
}
