package com.cherry.protocol.dto;

import com.cherry.protocol.enums.ClientType;
import lombok.Data;

/**
 * WebSocket 鉴权请求 DTO
 * 建议：前端连上后第一条消息先发送这个
 * @author cherry
 */
@Data
public class WsAuthReq {

    /**
     * 登录 token（比如你 HTTP 登录返回的那个）
     */
    private String token;

    /**
     * 端类型：web / ios / android / pc 等
     */
    private ClientType clientType;

    /**
     * 设备唯一标识：前端生成并缓存（localStorage）
     */
    private String deviceId;
}
