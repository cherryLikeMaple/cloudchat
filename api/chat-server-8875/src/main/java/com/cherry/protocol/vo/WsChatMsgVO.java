package com.cherry.protocol.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * WebSocket 推送给前端的单条聊天消息 VO
 * 服务端 -> 前端（包括自己和对方所有端）
 * @author cherry
 */
@Data
public class WsChatMsgVO {

    /**
     * 数据库自增 id
     */
    private Long id;

    /**
     * 前端发来的 msgId（方便前端做匹配）
     */
    private String clientMsgId;

    /**
     * 会话类型：1=单聊，2=群聊
     */
    private Integer chatType;

    /**
     * 发送者 / 接收者
     */
    private Long senderId;
    private Long receiverId;

    /**
     * 接收者类型：1=用户，2=群组
     */
    private Integer receiverType;

    /**
     * 消息类型
     */
    private Integer msgType;

    /**
     * 文本消息内容
     */
    private String msg;

    private String mediaPath;
    private Integer mediaWidth;
    private Integer mediaHeight;

    // ===== 视频相关 =====
    private Integer videoTimes;

    // ===== 语音相关 =====
    private String voicePath;
    private Integer speakVoiceDuration;

    /**
     * 消息时间
     */
    private LocalDateTime chatTime;

    /**
     * 是否已读：0=未读，1=已读
     */
    private Integer isRead;
    
    
}
