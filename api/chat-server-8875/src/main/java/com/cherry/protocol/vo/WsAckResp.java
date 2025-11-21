package com.cherry.protocol.vo;

import lombok.Data;

/**
 * 前端发消息后，服务端的 ack
 * @author cherry
 */
@Data
public class WsAckResp {

    /**
     * 前端发来的 msgId
     */
    private String clientMsgId;

    /**
     * 服务端生成的真正消息 id（如果你有数据库自增 id）
     */
    private Long serverMsgId;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 错误码 / 描述
     */
    private String errorMsg;
}
