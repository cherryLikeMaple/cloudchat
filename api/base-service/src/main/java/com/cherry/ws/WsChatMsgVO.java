package com.cherry.ws;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
    private String msgId;

    /**
     * 会话类型：1=单聊，2=群聊
     */
    private Integer chatType;

    /**
     * 发送者 / 接收者
     */
    private Long senderId;
    /**
     * 群聊: 群聊id, 单聊: 好友id
     */
    private Long receiverId;
    
    /**
     * 消息类型
     */
    private Integer msgType;

    /**
     * 文本消息内容
     */
    private String content;

    private String mediaUrl;
    private Integer mediaWidth;
    private Integer mediaHeight;

    // ===== 视频相关 =====
    private Integer videoTimes;
    private String videoCoverUrl;

    // ===== 语音相关 =====
    private String voiceUrl;
    private Integer voiceDuration;

    /**
     * 消息时间
     */
    private LocalDateTime chatTime;

    /**
     * 是否已读：0=未读，1=已读
     */
    private Integer isRead;
    
    
}
