package com.cherry.ws;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * WebSocket 发送聊天消息的 DTO
 * 前端 -> 服务端
 *
 * @author cherry
 */
@Data
public class WsChatSendReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 前端生成的消息唯一 ID，用来去重/匹配回执
     */
    private String msgId;

    private Long senderId;

    /**
     * 会话类型：1=单聊，2=群聊
     * 单聊这里前端传 1
     */
    private Integer chatType;

    /**
     * 接收者 id
     * - 单聊：对方用户 id
     * - 群聊：群 id
     */
    private Long receiverId;

    /**
     * 消息类型：1=文本，2=图片，3=视频，4=语音
     */
    private MsgType msgType;

    // ===== 文本消息字段 =====
    /**
     * 文本内容（TEXT 使用）
     */
    private String content;

    // ===== 图片/视频通用字段 =====
    /**
     * 图片/视频资源地址
     * - IMAGE：图片地址
     * - VIDEO：视频地址（映射到 video_path）
     */
    private String mediaUrl;

    /**
     * 图片/视频宽高（像素）
     * 对应表里的 video_width / video_height
     */
    private Integer mediaWidth;
    private Integer mediaHeight;
    /**
     * 视频特有的封面地址
     */
    private String videoCoverUrl;
    /**
     * 视频时长（秒），对应 video_times
     */
    private Integer videoDuration;

    // ===== 语音字段 =====
    /**
     * 语音资源地址，对应 voice_path
     */
    private String voiceUrl;

    /**
     * 语音时长（秒），对应 speak_voice_duration
     */
    private Integer voiceDuration;

    private LocalDateTime chatTime;
    
}
